package com.itellyou.util.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class TimestampDeserializer implements ObjectDeserializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        SimpleDateFormat dateFormat =  new SimpleDateFormat(parser.getDateFomartPattern());
          try {
              return value == null ? null : (T) DateUtils.getTimestamp(dateFormat.parse(value.toString()));
          }catch (Exception e){
              logger.error(e.getLocalizedMessage());
              return null;
          }
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
