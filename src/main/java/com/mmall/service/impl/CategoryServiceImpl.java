package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lijiajun1-sal on 2017/6/1.
 */
@Service("iCategoryServiceImpl")
public class CategoryServiceImpl implements ICategoryService {

    private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        //参数校验
        if(StringUtils.isBlank(categoryName)||parentId==null){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        if(parentId.intValue()!=0&&categoryMapper.selectByPrimaryKey(parentId)==null){
            return ServerResponse.createByErrorMessage("parentId不存在");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);


        int resultCount = categoryMapper.insert(category);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("增加品类失败");
        }
        return ServerResponse.createBySuccess("添加品类成功");

    }

    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId==null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category==null){
            return ServerResponse.createByErrorMessage("类别id不存在");
        }
        category.setName(categoryName);
        int updateResult = categoryMapper.updateByPrimaryKey(category);
        if(updateResult>0){
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        List<Category> categoryList = categoryMapper.selectByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        List<Integer> childrenIdList = Lists.newArrayList();
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = getChildrenCategory(categorySet,categoryId);
        for(Category category:categorySet){
            childrenIdList.add(category.getId());
        }
        return ServerResponse.createBySuccess(childrenIdList);
    }

    //递归算法，算出所有子节点
    public Set<Category> getChildrenCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查找子节点，mybatis如果没有查找到数据list，也不会返回null
        List<Category> childrenCategoryList = categoryMapper.selectByParentId(categoryId);
        if(CollectionUtils.isNotEmpty(childrenCategoryList)){
            for(Category item : childrenCategoryList){
                getChildrenCategory(categorySet,item.getId());
            }
        }
        return categorySet;
    }
}
