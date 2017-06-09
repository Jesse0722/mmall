package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by jesse on 2017/5/29.
 */
public class Const {
    public static final  String CURRENT_USER = "current_user";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
    }

    public interface Cart{
        int CHECKED=1;//购物车选中状态
        int UNCHECKED=0;//未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEnum{
        ON_SALE("在线",1);
        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public enum OrderStatusEnum{
        //'订单状态:0-已取消 10-未支付 20-已付款 40-已发货 50-交易成功 60-交易关闭'
        CANCEL("WAIT_BUYER_PAY",0),
        NO_PAY("TRADE_SUCCESS",10),
        PAID("TRADE_FINISHED",20),
        SHIPPED("TRADE_CLOSED",40),
        ORDER_SUCCESS("ORDER_SUCCESS",50),
        ORDER_CLOSED("ORDER_CLOSED",60);

        private String value;
        private int code;

        OrderStatusEnum(String value,int code){
            this.value=value;
            this.code=code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public interface AlipayTradeStatus{
        String TRADE_SUCCESS = "TRADE_SUCCESS";
        String WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_FINISHED="TRADE_FINISHED";
        String TRADE_CLOSED = "TRADE_CLOSED";
    }

    public interface AlipayResponse{
        String SUCCESS="success";
        String FAIL="fail";
    }

    public enum PayPlatform{
        ALIPAY("支付宝",1);

        private String value;
        private int code;

        PayPlatform(String value,int code){
            this.value=value;
            this.code=code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
