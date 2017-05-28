package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * Created by jesse on 2017/5/29.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

}
