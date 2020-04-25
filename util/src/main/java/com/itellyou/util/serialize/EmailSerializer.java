package com.itellyou.util.serialize;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.itellyou.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class EmailSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        String email = o == null ? "" : o.toString();
        int index = email.indexOf('@');
        if(StringUtils.isNotEmpty(email) && index > 0){
            String prefix = email.substring(0,index);
            if(prefix.length() > 3){
                email = prefix.substring(0,3) + "***" + email.substring(index);
            }else{
                email = prefix.substring(0,1) + "***" + email.substring(index);
            }
            jsonSerializer.write(email);
        }else{
            jsonSerializer.write(null);
        }
    }
}
