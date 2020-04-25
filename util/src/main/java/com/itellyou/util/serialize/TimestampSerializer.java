package com.itellyou.util.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.DateUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class TimestampSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type type, int i) throws IOException {
        if(object == null || object.equals(0l)){
            jsonSerializer.write(null);
        }else{
            Long value = (Long) object;
            String dateFormat = DateUtils.format(value,JSON.DEFFAULT_DATE_FORMAT);
            jsonSerializer.write(dateFormat);
        }
    }
}
