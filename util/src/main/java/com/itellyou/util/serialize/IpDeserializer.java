package com.itellyou.util.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.itellyou.util.IPUtils;

import java.lang.reflect.Type;

public class IpDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        try {
            return value == null ? null : (T) IPUtils.toLong(value.toString());
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
