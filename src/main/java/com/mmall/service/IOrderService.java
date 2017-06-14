package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderDetailVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/8.
 */
public interface IOrderService {
    ServerResponse<Map<String,String>> pay(Integer userId,Long orderNo,String path);

    ServerResponse<OrderVo> create(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

    ServerResponse<OrderDetailVo> detail(Integer userId, Long orderNo);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse<String> alipayCallback(Map<String,String> params);

    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse<PageInfo> searchByOrderNo(int pageNum,int pageSize,Long orderNo);

    ServerResponse<PageInfo> manageList( int pageNum, int pageSize);

    ServerResponse<OrderDetailVo> manageDetail(Long orderNo);

    ServerResponse<String> sendOrderGoods(Long orderNo);

    ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);
}
