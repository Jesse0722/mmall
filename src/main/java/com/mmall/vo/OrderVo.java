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
    private Integer shippingId;
    private BigDecimal payment;
    private Integer paymentType;
    private Integer postage;
    private Integer status;
    private Date paymentTime;
    private Date sendTime;
    private Date endTime;
    private Date closeTime;
    private Date createTime;

    private List<OrderItem> orderItemList;

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

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}