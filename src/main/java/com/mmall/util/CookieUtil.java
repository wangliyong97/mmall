package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangliyong on 2018/7/10.
 */

@Slf4j
public class CookieUtil {
    //private final static String COOKIE_DOMAIN = ".happymall.com";
    private final static String COOKIE_DOMAIN = "127.0.0.1";
    private final static String COOKIE_NAME = "mmall_login_token";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                //log.info("read cookie Name{} cookie Value{}",ck.getName(),ck.getValue());
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    log.info("return cookie name{} cookie value{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }

            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie ck = new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setMaxAge(60*60*24*365);  //设置有效期为一年   单位为秒
        ck.setPath("/");

        log.info("write cookie Name{} cookie value{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setMaxAge(0);
                    ck.setPath("/");
                    log.info("del cookie name{} cookie value{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return ;
                }
            }
        }
    }
}
