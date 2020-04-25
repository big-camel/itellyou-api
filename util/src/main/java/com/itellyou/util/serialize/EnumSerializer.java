package com.itellyou.util.serialize;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.BaseEnum;

import java.io.IOException;
import java.lang.reflect.Type;

public class EnumSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type type, int i) throws IOException {
        if(object == null){
            jsonSerializer.write(null);
        }else if(object instanceof BaseEnum){
            BaseEnum baseEnum  = (BaseEnum)object;
            jsonSerializer.write(baseEnum.getValue());
        }
    }
}
