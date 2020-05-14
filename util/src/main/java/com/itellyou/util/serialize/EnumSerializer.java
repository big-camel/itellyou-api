package com.itellyou.util.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.BaseEnum;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class EnumSerializer implements ObjectSerializer , ObjectDeserializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type type, int i) throws IOException {
        if(object == null){
            jsonSerializer.write(null);
        }else if(object instanceof BaseEnum){
            BaseEnum baseEnum  = (BaseEnum)object;
            jsonSerializer.write(baseEnum.getValue());
        }
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();

        try {
            Class clazz = Class.forName(type.getTypeName());
            Method method = clazz.getDeclaredMethod("valueOf",Integer.class);
            value = method.invoke(null,value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return (T)value;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
