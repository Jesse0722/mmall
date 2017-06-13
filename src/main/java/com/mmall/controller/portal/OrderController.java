package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderDetailVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lijiajun1-sal on 2017/6/8.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "pay.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),orderNo,path);
    }

    @RequestMapping("callback_test.do")
    @ResponseBody
    public String hello(){
        return "hello world";
    }

    @RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        //第一步： 在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数。
        //第二步： 将剩下参数进行url_decode, 然后进行字典排序，组成字符串，得到待签名字符串：
        //第三步： 将签名参数（sign）使用base64解码为字节码串。
        //第四步： 使用RSA的验签方法，通过签名字符串、签名参数（经过base64解码）及支付宝公钥验证签名。
        //第五步：需要严格按照如下描述校验通知数据的正确性。
        //商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        // 同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
        // 上述有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，
        // 并且过滤重复的通知结果数据。在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
        Map requestParams = Maps.newHashMap( request.getParameterMap());
        for(Iterator iterator=requestParams.keySet().iterator();iterator.hasNext();){
            String name = (String) iterator.next();
            String[] value = (String[]) requestParams.get(name);
            String valueStr="";
            for(int i=0;i<value.length;i++){
                valueStr += (i==value.length-1)?value[i]:value[i]+",";
            }
            requestParams.put(name,valueStr);
        }
        logger.info("支付宝回调，sign:{}，trade_status:{}，参数：{}",requestParams.get("sign"),requestParams.get("trade_status"),requestParams.toString());
        //验证回调，且避免重复通知
        requestParams.remove("sign_type");//签名类型写在配置文件中了，此处移除
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(requestParams, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }
        logger.info("RSA2验证通过，正在进行本地数据校验...");
        // 第五步验证各种数据是否正确
        ServerResponse response = iOrderService.alipayCallback(requestParams);
        if(response.isSuccess()){
            return Const.AlipayResponse.SUCCESS;
        }
        return Const.AlipayResponse.FAIL;
    }

    @RequestMapping(value = "query_order_pay_status.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(response.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


    @RequestMapping(value = "create.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<OrderVo> create(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.create(user.getId(),shippingId);
    }

    @RequestMapping(value = "cancel.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> cancel(HttpSession session, Long orderId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderId);
    }


    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.list(user.getId(),pageNum,pageSize);
    }

    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderDetailVo> detail(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.detail(user.getId(),orderNo);
    }

    @RequestMapping(value = "get_order_cart_product.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderProductVo> getOrderCartProduct(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

}
