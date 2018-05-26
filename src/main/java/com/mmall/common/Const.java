package com.mmall.common;

/**
 * Created by wangliyong on 2018/5/25.
 */
public class Const {
    public static final String Current_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
        //此处使用接口来取代繁重的枚举类
    }
}
