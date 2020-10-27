package com.itellyou.util.serialize;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.IPUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class IpSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        if(o == null){
            jsonSerializer.write("unknow");
        }else{
            Long ipLong = (Long)o;
            String ip = IPUtils.toString(ipLong);
            jsonSerializer.write(ip);
        }
    }
}
