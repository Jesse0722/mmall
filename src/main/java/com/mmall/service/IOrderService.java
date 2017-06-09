package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderDetailVo;

import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/8.
 */
public interface IOrderService {
    ServerResponse<Map> pay(Integer userId,Long orderNo,String path);

    ServerResponse<Long> create(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

    ServerResponse<OrderDetailVo> detail(Integer userId, Long orderNo);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse<String> alipayCallback(Map<String,String> params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderId);
}
