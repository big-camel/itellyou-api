package com.itellyou.util.serialize.filter;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.itellyou.util.StringUtils;

import java.lang.reflect.Field;

public class TimestampFilter implements ValueFilter {

    public Field getField(Object object, String name){
        Class clazz = object.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            }catch (NoSuchFieldException e){
                Field[] fields = clazz.getDeclaredFields();
                for (Field f : fields){
                    JSONField jsonField = f.getAnnotation(JSONField.class);
                    if(jsonField == null) continue;;
                    String filedName = jsonField.name();
                    String format = jsonField.format();
                    if(StringUtils.isNotEmpty(filedName) && filedName.equals(name) && StringUtils.isNotEmpty(format)){
                        return f;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public boolean isFormatTimestamp(Object object,String name) {
        Field field = getField(object,name);
        if (field != null && field.getType().equals(Long.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object process(Object o, String s, Object o1) {
        return null;
    }
}
