package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.List;
import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/6.
 */
public interface IShippingAddressService {
    ServerResponse<Map> add(Integer userId, Shipping shipping);
    ServerResponse<String> delete(Integer userId,Integer shippingId);
    ServerResponse<String> update(Integer userId,Shipping shipping);
    ServerResponse<Shipping> select(Integer userId,Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize);
}
