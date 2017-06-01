package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by lijiajun1-sal on 2017/6/1.
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(String categoryName ,Integer parentId);

    ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getDeepChildrenId(Integer categoryId);

}
