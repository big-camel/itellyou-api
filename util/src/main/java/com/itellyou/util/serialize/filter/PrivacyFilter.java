package com.itellyou.util.serialize.filter;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.Privacy;
import com.itellyou.util.validation.MobileValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import java.lang.reflect.Field;

public class PrivacyFilter implements ValueFilter {

    public Field getField(Object object,String name){
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
                    if(StringUtils.isNotEmpty(filedName) && filedName.equals(name)){
                        return f;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public boolean hasPrivacy(Object object,String name) {
        Field field = getField(object,name);
        if (field != null) {
            Privacy privacy = field.getAnnotation(Privacy.class);
            if(privacy != null){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object process(Object object, String name, Object value) {
        if(hasPrivacy(object,name) && value != null){
            String valueString = value.toString();
            if(new MobileValidator().isValid(valueString,null)){
                return valueString.substring(0,3) + "******" + valueString.substring(9);
            }else if(new EmailValidator().isValid(valueString,null)){
                int index = valueString.indexOf('@');
                String prefix = valueString.substring(0,index);
                if(prefix.length() > 2){
                    return prefix.substring(0,2) + "***" + valueString.substring(index);
                }else{
                    return prefix.substring(0,1) + "***" + valueString.substring(index);
                }
            }else{
                return  valueString.length() > 5 ?
                        valueString.substring(0,3) + "******" + valueString.substring(valueString.length() - 2) :
                        valueString.substring(0,1) + "******";
            }
        }
        return value;
    }
}
