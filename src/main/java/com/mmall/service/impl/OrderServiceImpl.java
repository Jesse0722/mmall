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
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(orderNo);
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "佳俊爸爸品牌当面付扫码消费";
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
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
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

                File fileDir = new File(path);
                if(!fileDir.exists()){
                    fileDir.setWritable(true);
                    fileDir.mkdir();
                }
                //生成二维码图片并上传ftpserver，拼接一个图片文件的路径
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(),256,qrPath);//生成二维码图片,路径在qrPath下
                //上传FTP
                File targetFile = new File(path,qrFileName);
                logger.info("开始上传支付二维码图片，上传文件的文件路径：{},文件名：{}",path,qrFileName);
                try {
                    //上生成的二维码保存到update文件中
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常",e);
                }
                resultMap.put("qrUrl", PropertiesUtil.getProperty("ftp.server.http.prefix")+qrFileName);
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
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public ServerResponse<OrderVo> create(Integer userId, Integer shippingId) {
        //地址必须要属于当前登录用户
        if(userId==null||shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByIdAndUserId(shippingId,userId);
        if(shipping==null) {
            return ServerResponse.createByErrorMessage("收货地址信息错误");
        }
        //创建订单：结算当前购物车已勾选的商品
        List<Cart> checkedCartList = cartMapper.selectCartProductCheckedByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem(userId,checkedCartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList =this.getCartOrderItem(userId,checkedCartList).getData();
        //计算金额
        BigDecimal payment = this.getTotalPayment(orderItemList);
        //更新库存
        List<Product> productList = this.batchSetProductStock(orderItemList);
        //组装订单
        Order order = this.assembleOrder(userId,shippingId,payment);
        //执行数据库操作：订单、订单单项、商品库存
        int resultCount = orderMapper.insert(order);
        if(resultCount==0){
            return  ServerResponse.createByErrorMessage("生成订单号错误");
        }
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //更新orderItem订单号
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //批量插入订单单项
        orderItemMapper.insertBatch(orderItemList);
        //批量更新库存
        productMapper.updateStockByList(productList);
        //清空购物车
        cartMapper.deleteBatch(checkedCartList);
        //返回给前段数据
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    /***
     * 获取订单单项
     * @param userId
     * @param cartList 购物车中已勾选的购物项
     * @return
     */
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for(Cart cart : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //如果商品在线状态
            if(product.getStatus()!=Const.ProductStatusEnum.ON_SALE.getCode()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在线销售状态");
            }
            //校验库存
            if(product.getStock()<cart.getQuantity()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());//商品购买价格的快照，因为商品价格是浮动的
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    private BigDecimal getTotalPayment(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        long orderNo = this.generateOrderNo();

        Order order  = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(Const.PlaymentType.PAY_ONLINE.getCode());//这里前台要传一个支付方式过来，这个支付方式要在枚举类型中，否则报错
        order.setPostage(10);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        return order;
    }
    private List<Product> batchSetProductStock(List<OrderItem> OrderItemList){
        List<Product> productList = Lists.newArrayList();
        for(OrderItem orderItem:OrderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productList.add(product);
        }
        return productList;
    }


    private long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
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
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,userId);
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
        orderVo.setPaymentType(Const.PlaymentType.codeOf(order.getPaymentType()).getCode());
        orderVo.setPaymentTypeDesc(Const.PlaymentType.codeOf(order.getPaymentType()).getValue());
        orderVo.setCreateTime(DateTimeUtil.DateToStr(order.getCreateTime()));
        orderVo.setEndTime(DateTimeUtil.DateToStr(order.getEndTime()));
        orderVo.setPaymentTime(DateTimeUtil.DateToStr(order.getPaymentTime()));
        orderVo.setCloseTime(DateTimeUtil.DateToStr(order.getCloseTime()));
        orderVo.setSendTime(DateTimeUtil.DateToStr(order.getSendTime()));
        orderVo.setOrderItemList(orderItemList);
        orderVo.setShippingId(order.getShippingId());
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return orderVo;
    }


    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for(Order order: orderList){
            List<OrderItem> orderItemList ;
            if(userId==null){
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());//管理员查询
            }else{
                orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,order.getOrderNo());
            }
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
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
        Long outTradeNo = Long.parseLong(params.get("out_trade_no"));//订单号
        String totalAmount = params.get("total_amount");//总金额
        String tradeStatus = params.get("trade_status");//订单状态
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
        if(Const.AlipayTradeStatus.TRADE_SUCCESS.equals(tradeStatus)&&
                BigDecimalUtil.equal(order.getPayment().doubleValue(),Double.parseDouble(totalAmount))){
            logger.info("支付回调状态成功，交易状态：{}",tradeStatus);
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            orderMapper.updateByPrimaryKey(order);
        }
        //创建支付信息记录，每次回调都创建一条支付信息
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
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
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo){
        if(userId==null||orderNo==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        logger.info("userId:{} , orderNo:{}",userId,orderNo);
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("无此订单信息");
        }
        if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    /***
     * 根据订单号查询订单
     * @param pageNum
     * @param pageSize
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchByOrderNo(int pageNum,int pageSize,Long orderNo){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.searchByOrderNo(orderNo);
        if(order != null){
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo  orderVo = assembleOrderVo(order,orderItemList);

            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageInfo);

        }
        return ServerResponse.createByErrorMessage("订单不存在");

    }

    /***
     * 管理员订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> manageList( int pageNum, int pageSize) {
        //获取订单列表
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /***
     * 管理员订单详情
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<OrderDetailVo> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order!=null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderDetailVo orderDetailVo = this.assembleOrderDetailVo(order,orderItemList,order.getShippingId());
            return ServerResponse.createBySuccess(orderDetailVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<String> sendOrderGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order!=null) {
            if(order.getStatus()==Const.OrderStatusEnum.PAID.getCode()) {
                order.setSendTime(new Date());
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /***
     * 在购物车中获取订单的商品信息
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId){
        //获取订单中订单单项，组装orderItemVoList
        OrderProductVo orderProductVo = new OrderProductVo();

        List<Cart> cartList = cartMapper.selectByUserId(userId);
        ServerResponse serverRespons = this.getCartOrderItem(userId,cartList);
        if(!serverRespons.isSuccess()){
            return serverRespons;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverRespons.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCreateTime(DateTimeUtil.DateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
}
