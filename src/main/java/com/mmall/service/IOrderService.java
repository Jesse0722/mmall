package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.vo.OrderDetailVo;
import com.mmall.vo.OrderListVo;

import java.util.List;
import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/8.
 */
public interface IOrderService {
    ServerResponse<Map> pay(Integer userId,Long orderNo,String path);

    ServerResponse<String> create(Integer userId,Integer shippingId);

    ServerResponse<List<OrderListVo>> list(Integer userId);

    ServerResponse<OrderDetailVo> detail(Integer userId, Integer orderNo);

    ServerResponse<String> cancel(Integer userId,Integer orderNo);
}
