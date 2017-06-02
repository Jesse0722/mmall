package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lijiajun1-sal on 2017/6/2.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product == null ){
            return ServerResponse.createByErrorMessage("新增产品或更新产品参数错误");
        }
        //根据子图片设置主图片逻辑
        if(StringUtils.isNotBlank(product.getSubImages())){
            String[] subImageArray = product.getSubImages().split(",");
            if(subImageArray.length>0){
                product.setMainImage(subImageArray[0]);
            }
        }
        //根据id来判断是更新还是保存操作
        if(product.getId()!=null){
            //更新操作
            int updateCount = productMapper.updateByPrimaryKey(product);
            if(updateCount>0){
                return ServerResponse.createBySuccess("更新产品成功");
            }
            return ServerResponse.createByErrorMessage("更新产品失败");
        }else{
            //保存操作
            int insertCount = productMapper.insert(product);
            if(insertCount>0){
                return ServerResponse.createBySuccess("保存产品成功");
            }
            return ServerResponse.createByErrorMessage("保存产品失败");
        }

    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数不合法");
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int updateCount = productMapper.updateByPrimaryKeySelective(product);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("更新产品状态成功");
        }
        return ServerResponse.createByErrorMessage("更新产品状态失败");
    }

    @Override
    public ServerResponse getDetail(Integer productId) {
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数不合法");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product!=null){
            return ServerResponse.createBySuccess(product);
        }
        return ServerResponse.createByErrorMessage("产品信息不存在");
    }

}
