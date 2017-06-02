package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * Created by jesse on 2017/5/29.
 */
public interface IUserService {
    /***
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /***
     * 注册账号
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /***
     * 验证用户名、邮箱是否有效
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str,String type);

    /***
     * 获取设置问题
     * @param username
     * @return
     */
    ServerResponse<String> getQuestion(String username);

    /***
     * 验证答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username,String question, String answer);

    /***
     * 忘记密码-重置密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken);

    /***
     * 在线重置密码
     * @param user
     * @param password
     * @param newPassword
     * @return
     */
    ServerResponse<String> resetPassword(User user,String password,String newPassword);

    /***
     * 更新账户信息
     * @param user
     * @return
     */
    ServerResponse<User> updateInformation(User user);

    /***
     * 获取用户信息
     * @param userId
     * @return
     */
    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);

    /***
     * 管理员用户注册，需要验证注册码，由系统管理员生成
     * @return
     */
    ServerResponse<String> generateRegisterCode();

    boolean checkRegisterCode(String registerCode);
}
