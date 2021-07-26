package com.itellyou.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CookieUtils {

    public static Cookie getCookie(HttpServletRequest request, String name){
        Map<String, Cookie> map = toMap(request);
        if (map.containsKey(name)) {
            try {
                Cookie cookie = map.get(name);
                String value = cookie.getValue();
                if(StringUtils.isNotEmpty(value)){
                    value = URLDecoder.decode(value, "utf-8");
                    cookie.setValue(value);
                }
                return cookie;
            } catch (UnsupportedEncodingException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String, Cookie> toMap(HttpServletRequest request) {
        Map<String, Cookie> map = new HashMap<String , Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie);
            }
        }
        return map;
    }

    public static HttpServletResponse setCookie(HttpServletResponse response, String name, String value,String path, int time) {
        Cookie cookie = null;
        try {
            if(StringUtils.isNotEmpty(value)){
                value = URLEncoder.encode(value, "utf-8");
            }
            cookie = new Cookie(name, value);
            cookie.setPath(path);
            String springProfilesActive = SpringContextUtils.getActiveProfile();
            if(StringUtils.isNotEmpty(springProfilesActive) && "prod".equals(springProfilesActive)){
                cookie.setDomain("yanmao.cc");
            }else{
                cookie.setDomain("localhost");
            }
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        }
        cookie.setMaxAge(time);
        response.addCookie(cookie);
        return response;
    }

    public static HttpServletResponse setCookie(HttpServletResponse response, String name, String value,String path){
        return setCookie(response, name, value, path,3600);
    }

    public static HttpServletResponse setCookie(HttpServletResponse response, String name, String value){
        return setCookie(response, name, value, "/",3600);
    }

    public static HttpServletResponse removeCookie(HttpServletResponse response, String name){
        return setCookie(response, name, null, "/",0);
    }
}
