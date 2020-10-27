package com.itellyou.util.serialize.config;

import com.alibaba.fastjson.serializer.SerializerFeature;

public class SerializerFeatureConfig {
    public static SerializerFeature[] getDefault(){
        return new SerializerFeature[]{
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                // Number null -> 0
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteDateUseDateFormat,
                //禁止循环引用
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteEnumUsingToString,
                SerializerFeature.UseISO8601DateFormat
        };
    }
}
