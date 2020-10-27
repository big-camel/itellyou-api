package com.itellyou.util.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class TimestampSerializer implements ObjectSerializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type type, int i) {
        try {
            if (object == null || object.equals(0l)) {
                jsonSerializer.write(null);
            } else {
                String format = jsonSerializer.getDateFormatPattern();
                Long value = (Long) object;
                if (StringUtils.isEmpty(format)) format = JSON.DEFFAULT_DATE_FORMAT;
                String dateFormat = DateUtils.format(value, format);
                jsonSerializer.write(dateFormat);
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
