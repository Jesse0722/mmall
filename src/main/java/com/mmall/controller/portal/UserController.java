package com.mmall.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by jesse on 2017/5/29.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public Object login(String username,String password,HttpSession session){
        return null;
    }
}
