package com.mall.service;

import com.mmall.common.Const;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by lijiajun1-sal on 2017/6/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class OrderServiceTest {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IOrderService iOrderService;

    @Transactional
    @Test
    public void updateOrder(){
        Order order =orderMapper.selectByPrimaryKey(5);
        order.setStatus(5);
        orderMapper.updateByPrimaryKey(order);
       // this.test();
        System.out.print(order.getOrderNo());

    }

    @Test
    public void equalTest(){
        Order order = orderMapper.selectByPrimaryKey(18);
        boolean flag=BigDecimalUtil.equal(order.getPayment().doubleValue(), Double.parseDouble("30000.00"));
        System.out.print(flag);
        flag=order.getPayment().equals(new BigDecimal("30000.00"));
        System.out.print(flag);
    }
//
//    @Transactional
//    @Test
//    public void createOrder(){
//        Order order = new Order();
//        order.setUserId(21);
//        order.setPayment(new BigDecimal("1000"));
//        order.setOrderNo(new Long(100000000).longValue());
//        order.setShippingId(3);
//
//        orderMapper.insert(order);
//    }
//    private void test(){
//        System.out.println("私有方法");
//    }
}
