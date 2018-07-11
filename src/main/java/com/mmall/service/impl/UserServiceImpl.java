package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by wangliyong on 2018/5/25.
 */

@Slf4j
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码登录MD5
        String MD5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, MD5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }



    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        //设置账户类型
        user.setRole(Const.role.ROLE_CUSTOMER);

        //MD5 非对称加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }


    public ServerResponse<String> checkValid(String str, String type){
        //isNotBlank函数和isEmpty区别前者将“ ”等于false后者相等于true
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已被注册");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    public ServerResponse selectQuestion(String username){
        ServerResponse<String> validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题为空");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明验证答案正确
            String forgetToken = UUID.randomUUID().toString();
            log.info("forgetToken {}", forgetToken);

            RedisPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*12);
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return ServerResponse.createByErrorMessage("答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要重传");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或已过期");
        }

        if(StringUtils.equals(forgetToken,token)){
            String md5password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowcount = userMapper.updatePasswordByUsername(username, md5password);
            if(rowcount>0){
                RedisPoolUtil.del(Const.TOKEN_PREFIX+username);
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else{
                return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
            }
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew, User user){
        String md5passwordOld = MD5Util.MD5EncodeUtf8(passwordOld);
        int resultCount = userMapper.checkPassword(md5passwordOld, user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    public ServerResponse<User> updateInformation(User user){
        //不能更新username
        //需要检验email
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email已被注册，请更换email后再试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend
    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user!=null && user.getRole().intValue() == Const.role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}