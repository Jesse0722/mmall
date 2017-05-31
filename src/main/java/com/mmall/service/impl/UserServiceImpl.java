package com.mmall.service.impl;

import com.mmall.util.MD5Util;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by jesse on 2017/5/29.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //// TODO: 2017/5/29 密码登录md5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccessData("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user){

        ServerResponse validResponse = checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess())
            return validResponse;
        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess())
            return validResponse;

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNoneBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> getQuestion(String username){
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空");

    }

    @Override
    public ServerResponse<String> checkAnswer(String username,String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            //缓存forgetToken到本机
            TokenCache.setKey("token_"+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，forgetToken需要传递");
        }
        String tokenCache = TokenCache.getKey("token_"+username);
        if(!StringUtils.equals(tokenCache,forgetToken)){
            return ServerResponse.createByErrorMessage("forgetToken无效");
        }
        int resultCount = userMapper.updatePasswordByUsername(username, MD5Util.MD5EncodeUtf8(newPassword));
        if(resultCount>0){
            return ServerResponse.createBySuccessMessage("重置密码成功");
        }

        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String password, String newPassword) {
        //横向校验
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(password));
        if(resultCount>0){
            //密码验证成功
            user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
            int updateCount =  userMapper.updateByPrimaryKeySelective(user);
            if(updateCount>0){
                return ServerResponse.createBySuccessMessage("重置密码成功");
            }
            return ServerResponse.createByErrorMessage("重置密码失败");
        }
        return ServerResponse.createByErrorMessage("密码验证失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user){
        //邮箱需要校验，其他用户已注册的邮箱不允许修改
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已被注册，请重新修改");
        }
        //保证user不为空
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("个人信息更新成功");
        }
        return ServerResponse.createByErrorMessage("个人信息更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("获取用户信息失败");
        }
        //隐藏返回密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

}
