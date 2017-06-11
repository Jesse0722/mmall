package com.mmall.vo;

import com.mmall.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by lijiajun1-sal on 2017/6/8.
 */
public class OrderVo {
//   {"orderNo":1496997308617,"payment":6999.00,"paymentType":1,"paymentTypeDesc":"在线支付","postage":0,"status":10,"statusDesc":"未支付",
//           "paymentTime":"","sendTime":"","endTime":"","closeTime":"","createTime":"2017-06-09 16:35:08",
//           "orderItemVoList":[{"orderNo":1496997308617,"productId":26,"productName":"Apple iPhone 7 Plus (A1661) 128G手机",
//           "productImage":"241997c4-9e62-4824-b7f0-7425c3c28917.jpeg","currentUnitPrice":6999.00,"quantity":1,"totalPrice":6999.00,
//           "createTime":"2017-06-09 16:35:08"},{"orderNo":1496997308617,"productId":31,
//           "productName":"【特卖】魅族 魅蓝E2 4GB+64GB 全网通公开版 香槟金 移动联通电信4G手机 双卡双待",
//           "productImage":"7d71fc85-1d6a-4613-b9b7-c8956d8a38d5.jpg","currentUnitPrice":1499.00,"quantity":0,
//           "totalPrice":0.00,"createTime":"2017-06-09 16:35:08"}],"imageHost":"http://img.happymmall.com/"
//           ,"shippingId":304,"receiverName":"李佳俊","shippingVo":{"receiverName":"李佳俊","receiverPhone":null,"receiverMobile":null,"receiverProvince":"重庆","receiverCity":"重庆",
//           "receiverDistrict":null,"receiverAddress":"渝北区松石北路232号东和春天B1","receiverZip":"400056"}},

    private Long orderNo;
    private BigDecimal payment;
    private Integer paymentType;
    private String paymentTypeDesc;
    private Integer postage;
    private Integer status;
    private String paymentTime;
    private String sendTime;
    private String endTime;
    private String closeTime;
    private String createTime;

    private List<OrderItem> orderItemList;

    private String imageHost;
    private Integer shippingId;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }
    public String getPaymentTypeDesc() {
        return paymentTypeDesc;
    }

    public void setPaymentTypeDesc(String paymentTypeDesc) {
        this.paymentTypeDesc = paymentTypeDesc;
    }
    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

}