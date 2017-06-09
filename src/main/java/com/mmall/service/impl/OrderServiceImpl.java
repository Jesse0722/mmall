package com.mmall.service.impl;


import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderDetailVo;
import com.mmall.vo.OrderVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/8.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    static{
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }



    @Override
    public ServerResponse<Map> pay(Integer userId, Long orderNo, String path) {
        if(userId==null||orderNo==null||StringUtils.isBlank(path)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Map<String,String> resultMap = Maps.newHashMap();
        resultMap.put("orderNo",String.valueOf(orderNo));

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("未生成订单号");
        }
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(orderNo);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "xxx品牌xxx门店当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品3件共20.00元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";
        List<GoodsDetail> goodsDetailList = Lists.newArrayList();

        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        for(OrderItem orderItem:orderItemList){
            GoodsDetail goodsDetail =GoodsDetail.newInstance(orderItem.getProductId().toString(),
                     orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(order.getPayment().toString()).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()){
            case SUCCESS:
                logger.info("支付宝预下单成功");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                //生成二维码图片并上传ftpserver，拼接一个图片文件的路径
                String fileName = String.format("qr-%s.png",response.getOutTradeNo());
                String filePath = new StringBuilder().append(path).append("/").append(fileName).toString();
                File qrCodeImage =  ZxingUtils.getQRCodeImge(response.getQrCode(),256,filePath);//生成二维码图片
                //上传FTP
                logger.info("开始上传支付二维码图片，上传文件的文件路径名：{}",filePath);
                File fileDir = new File(filePath);
                if(!fileDir.exists()){
                    fileDir.setWritable(true);
                    fileDir.mkdir();
                }
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(qrCodeImage));
                } catch (IOException e) {
                    logger.error("上传文件异常",e);
                }
                resultMap.put("qrUrl", PropertiesUtil.getProperty("ftp.server.http.prefix")+fileName);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝支付失败");
                return ServerResponse.createByErrorMessage("系统异常，订单状态未知!!!");
            case UNKNOWN:
                logger.error("支付宝支付失败");
                return ServerResponse.createByErrorMessage("系统异常，订单状态未知!!!");

            default:
                logger.error("支付宝支付失败");
                return ServerResponse.createByErrorMessage("系统异常，订单状态未知!!!");
        }

    }
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (org.apache.commons.lang.StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }


    /***
     * 创建订单：为每个商品创建订单单项OrderItem,修改每个商品Product库存，创建一个订单Order，此过程应该是事务处理
     * @param userId
     * @param shippingId
     * @return
     */
    @Transactional
    @Override
    public ServerResponse<Long> create(Integer userId, Integer shippingId) {
        //地址必须要属于当前登录用户
        if(userId==null||shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByIdAndUserId(userId,shippingId);
        if(shipping==null) {
            return ServerResponse.createByErrorMessage("收货地址信息错误");
        }
        //创建订单：结算当前购物车已勾选的商品
        List<Cart> checkedCartList = cartMapper.selectCartProductCheckedByUserId(userId);
        List<OrderItem> orderItemList = Lists.newArrayList();
        List<Product> productList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        Long orderNo = this.generateOrderNo();
        for(Cart cart : checkedCartList){
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //如果商品在线且同时库存也要足够
            if(product.getStatus()==1&&product.getStock()>cart.getQuantity()) {
                product.setStock(product.getStock() - cart.getQuantity());//商品减库存
                productList.add(product);
                //生成商品订单单项
                OrderItem orderItem = new OrderItem();
                orderItem.setUserId(userId);
                orderItem.setOrderNo(orderNo);
                orderItem.setProductId(cart.getProductId());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));
                orderItemList.add(orderItem);
                //累计商品总价
                payment = BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(), payment.doubleValue());
            }
        }
        //批量更新库存
        productMapper.updateStockByList(productList);
        //批量插入订单单项
        int insertCount = orderItemMapper.insertBatch(orderItemList);
        if(insertCount==0){
            //抛出异常创建订单单项失败
        }
        //创建订单
        Order order  = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(1);
        order.setPostage(10);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());

        insertCount = orderMapper.insert(order);
        if(insertCount==0){
            //订单创建异常
        }

        return ServerResponse.createBySuccess(orderNo);
    }

    private Long generateOrderNo(){
        return System.currentTimeMillis() + (long) (Math.random() * 10000000L);
    }

    /***
     * 获取订单列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        //获取订单列表
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);

        List<OrderVo> orderVoList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(orderList)){
            for(Order order : orderList){
                //组装OrderVo
                List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,order.getOrderNo());
                OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
                orderVoList.add(orderVo);
            }
        }
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setStatus(order.getStatus());
        orderVo.setPayment(order.getPayment());
        orderVo.setPostage(order.getPostage());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setEndTime(order.getEndTime());
        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setCloseTime(order.getCloseTime());
        orderVo.setSendTime(order.getSendTime());
        orderVo.setOrderItemList(orderItemList);
        orderVo.setShippingId(order.getShippingId());

        return orderVo;
    }

    private OrderDetailVo assembleOrderDetailVo(Order order,List<OrderItem> orderItemList,Integer shippingId){
        OrderDetailVo orderDetailVo =  new OrderDetailVo();
        orderDetailVo.setOrderNo(order.getOrderNo());
        orderDetailVo.setStatus(order.getStatus());
        orderDetailVo.setPayment(order.getPayment());
        orderDetailVo.setPostage(order.getPostage());
        orderDetailVo.setPaymentType(order.getPaymentType());
        orderDetailVo.setCreateTime(order.getCreateTime());
        orderDetailVo.setEndTime(order.getEndTime());
        orderDetailVo.setPaymentTime(order.getPaymentTime());
        orderDetailVo.setCloseTime(order.getCloseTime());
        orderDetailVo.setSendTime(order.getSendTime());
        orderDetailVo.setOrderItemList(orderItemList);
        orderDetailVo.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        orderDetailVo.setShipping(shipping);

        return orderDetailVo;
    }

    /***
     * 获取订单详情：地址信息，订单信息，商品列表信息
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<OrderDetailVo> detail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        OrderDetailVo orderDetailVo = this.assembleOrderDetailVo(order,orderItemList,order.getShippingId());
        return ServerResponse.createBySuccess(orderDetailVo);
    }

    /***
     * 取消订单：订单状态修改为已取消，同时需要恢复商品原有库存，事务处理
     * @param userId
     * @param orderNo
     * @return
     */
    @Transactional
    @Override
    public ServerResponse<String> cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("取消订单信息错误");
        }
        order.setStatus(Const.OrderStatusEnum.CANCEL.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(order);
        if(resultCount>0){
            //取消成功，更改库存状态
            List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
            List<Product> productList = Lists.newArrayList();
            for(OrderItem orderItem:orderItemList){
                Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
                product.setStock(product.getStock()+orderItem.getQuantity());
                productList.add(product);
            }
            productMapper.updateStockByList(productList);
            return ServerResponse.createBySuccessMessage("取消订单成功");
        }
        return ServerResponse.createBySuccessMessage("取消订单失败");
    }


    /***
     * 当支付宝回调验证通过是，自己也对参数交易号、进行验证
     * @param params
     * @return
     */
    @Override
    public ServerResponse<String> alipayCallback(Map<String,String> params) {
        Long outTradeNo = Long.parseLong(params.get("out_trade_no"));
        String totalAmount = params.get("total_amount");
        String tradeStatus = params.get("trade_status");
        String tradeNo = params.get("trade_no");//支付宝交易号，交易成功需持久化到payInfo

        Order  order = orderMapper.selectByOrderNo(new Long(outTradeNo).longValue());
        if(order==null){
            return ServerResponse.createByErrorMessage("订单号不存在，验证失败");
        }
        //判断订单状态：如果订单已经是支付过了，则回复success，通知支付宝不要再通知了
        if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("订单已支付，请勿重复通知");
        }
        //判断支付状态,如果是支付成功则更改订单状态
        if(Const.AlipayTradeStatus.TRADE_SUCCESS.equals(tradeStatus)){
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            orderMapper.updateByPrimaryKey(order);
        }
        //创建支付信息记录，每次回调都创建一条支付信息
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
        payInfo.setPlatformStatus(tradeStatus);
        payInfo.setPlatformMumber(tradeNo);
        payInfoMapper.insert(payInfo);

        return  ServerResponse.createBySuccess();
    }

    /***
     * 查看订单支付状态
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderId){
        if(userId==null||orderId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderId);
        if(order == null){
            return ServerResponse.createByErrorMessage("无此订单信息");
        }
        if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
