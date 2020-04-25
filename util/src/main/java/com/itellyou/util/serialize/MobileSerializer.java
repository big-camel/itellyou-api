package com.itellyou.util.serialize;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class MobileSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        String mobile = o == null ? "" : o.toString();
        if(mobile.length() != 11){
            jsonSerializer.write(null);
        }else{
            mobile = mobile.substring(0,3) + "******" + mobile.substring(9);
            jsonSerializer.write(mobile);
        }
    }
}
