package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by lijiajun1-sal on 2017/6/6.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingAddressController {

    @Autowired
    private IShippingAddressService iShippingAddressService;
    @RequestMapping(value = "add.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingAddressService.add(user.getId(),shipping);
    }

    @RequestMapping(value = "delete.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse<String> delete(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingAddressService.delete(user.getId(),shippingId);
    }

    @RequestMapping(value = "update.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse<String> update(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingAddressService.update(user.getId(),shipping);
    }

    @RequestMapping(value = "select.do",method = RequestMethod.GET )
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingAddressService.select(user.getId(),shippingId);
    }

    @RequestMapping(value = "list.do",method = RequestMethod.GET )
    @ResponseBody
    public ServerResponse select(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageNum",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingAddressService.list(user.getId(),pageNum,pageSize);
    }
}
