package com.itellyou.util;

import java.util.Map;

public class Params {

    public static String getOrDefault(Map map,String key,Object defaultValue){
        return getOrDefault(map,key,String.class,defaultValue);
    }

    public static <T> T getOrDefault(Map map,String key,Class<T> clazz,Object defaultValue){
        Object value = map.getOrDefault(key,defaultValue);
        if(value == null || StringUtils.isEmpty(value.toString())) return null;
        String typeName = clazz.getTypeName();
        final String booleanTypeName = "java.lang.Boolean";
        if (booleanTypeName.equals(typeName)) {
            return (T)Boolean.valueOf(value.toString());
        }
        final String intTypeName = "java.lang.Integer";
        if (intTypeName.equals(typeName)) {
            return (T)Integer.valueOf(value.toString());
        }
        final String charTypeName = "java.lang.Char";
        if (charTypeName.equals(typeName)) {
            return clazz.cast(value.toString().charAt(0));
        }
        final String shortTypeName = "java.lang.Short";
        if (shortTypeName.equals(typeName)) {
            return (T)Short.valueOf(value.toString());
        }
        final String longTypeName = "java.lang.Long";
        if (longTypeName.equals(typeName)) {
            return (T)Long.valueOf(value.toString());
        }
        final String floatTypeName = "java.lang.Float";
        if (floatTypeName.equals(typeName)) {
            return (T)Float.valueOf(value.toString());
        }
        final String doubleTypeName = "java.lang.Double";
        if (doubleTypeName.equals(typeName)) {
            return (T)Double.valueOf(value.toString());
        }
        final String byteTypeName = "java.lang.Byte";
        if (byteTypeName.equals(typeName)) {
            return (T)Byte.valueOf(value.toString());
        }
        return clazz.cast(value);
    }

}
