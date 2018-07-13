package com.mmall.controller.backend;

import com.github.pagehelper.StringUtil;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by wangliyong on 2018/5/26.
 */

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    public IUserService iUserService;
    @Autowired
    public ICategoryService iCategoryService;


    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpServletRequest httpServletRequest, String categoryName, @RequestParam(value = "parentId", defaultValue = "0")int parentId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取相关信息");
        }
        String userjsonString = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.String2obj(userjsonString,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //如果success则是管理员
            //增加分类逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，请用管理员账号登录");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpServletRequest httpServletRequest,Integer categoryId, String categoryName){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取相关信息");
        }
        String userjsonString = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.String2obj(userjsonString,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCatagoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，请用管理员账号登录");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest httpServletRequest,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取相关信息");
        }
        String userjsonString = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.String2obj(userjsonString,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，请用管理员账号登录");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepCategory(HttpServletRequest httpServletRequest,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取相关信息");
        }
        String userjsonString = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.String2obj(userjsonString,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询递归节点的id以及子节点id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，请用管理员账号登录");
        }
    }
}
