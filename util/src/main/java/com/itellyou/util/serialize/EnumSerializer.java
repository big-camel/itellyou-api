package com.itellyou.util.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class EnumSerializer implements ObjectSerializer , ObjectDeserializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type type, int i) throws IOException {
        if(object == null){
            jsonSerializer.write(null);
        }else{
            jsonSerializer.write(object.toString());
        }
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        if(value == null) return null;
        try {
            Class clazz = Class.forName(type.getTypeName());
            Method method = clazz.getDeclaredMethod("valueOf",String.class);
            value = method.invoke(null,value.toString().toUpperCase());
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
        return (T)value;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
