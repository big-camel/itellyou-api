package com.itellyou.util.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.itellyou.util.DateUtils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class TimestampDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        SimpleDateFormat format =  new SimpleDateFormat(JSON.DEFFAULT_DATE_FORMAT);
          try {
              return value == null ? null : (T) DateUtils.getTimestamp(format.parse(value.toString()));
          }catch (Exception e){
              return null;
          }
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
