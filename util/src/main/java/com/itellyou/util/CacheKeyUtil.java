package com.itellyou.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CacheKeyUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheKeyUtil.class);

    public static String getCacheKey(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class clazz = obj.getClass();
        if (clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(HashMap.class) ||
                clazz.equals(ArrayList.class) ||
                clazz.equals(Short.class)
        ) {
            return obj.toString();
        }

        try {
            Method method = clazz.getDeclaredMethod("cacheKey");
            return (String) method.invoke(obj);
        } catch (Exception e) {
            logger.warn("invoke cacheKey method fail, use default toString as cacheKey:", e);
            return obj.toString();
        }
    }
}