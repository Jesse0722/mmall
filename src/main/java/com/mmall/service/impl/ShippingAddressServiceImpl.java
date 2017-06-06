package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingAddressService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/6.
 */
@Service("iShippingAddressService")
public class ShippingAddressServiceImpl implements IShippingAddressService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId,Shipping shipping){
        if(userId == null || shipping==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);

        int insertCount = shippingMapper.insert(shipping);
        if(insertCount>0){
            Map map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccessData("新建地址成功",map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse<String> delete(Integer userId,Integer shippingId){
        if(userId == null || shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int resultCount = shippingMapper.deleteByUserIdAndPrimaryKey(userId,shippingId);
        if(resultCount>0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse<String> update(Integer userId,Shipping shipping){
        if(userId == null || shipping==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);//防止伪造shipping中用户id
        int updateCount = shippingMapper.updateByShipping(shipping);//重置userId后，sql的查找条件加上userId防止横向越权
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createBySuccessMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        if(userId == null || shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByIdAndUserId(shippingId,userId);//只能查自己的地址，参数应该是userId加shippingId
        if(shipping!=null){
            return ServerResponse.createBySuccessData("查询地址成功",shipping);
        }
        return ServerResponse.createByErrorMessage("地址不存在");
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize) {
        if(userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping>  shippingList= shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
