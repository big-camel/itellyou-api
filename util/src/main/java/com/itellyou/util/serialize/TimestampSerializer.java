package com.itellyou.util.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerialContext;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class TimestampSerializer implements ObjectSerializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type type, int i) throws IOException {
        if(object == null || object.equals(0l)){
            jsonSerializer.write(null);
        }else{
            String format = null;
            Long value = (Long) object;
            if(StringUtils.isEmpty(format)) format = JSON.DEFFAULT_DATE_FORMAT;
            String dateFormat = DateUtils.format(value,format);
            jsonSerializer.write(dateFormat);
        }
    }
}
