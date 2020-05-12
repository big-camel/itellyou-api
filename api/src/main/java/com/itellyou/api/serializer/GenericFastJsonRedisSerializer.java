package com.itellyou.api.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.itellyou.util.serialize.config.SerializerFeatureConfig;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class GenericFastJsonRedisSerializer implements RedisSerializer<Object> {
    private final static ParserConfig defaultRedisConfig = new ParserConfig();
    static { defaultRedisConfig.setAutoTypeSupport(true);}

    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }
        try {
            SerializerFeature[] defaults = SerializerFeatureConfig.getDefault();
            SerializerFeature[] serializerFeatures = new SerializerFeature[defaults.length + 1];
            for (int i = 0; i < defaults.length; i++){
                serializerFeatures[i] = defaults[i];
            }
            serializerFeatures[defaults.length] = SerializerFeature.WriteClassName;
            return JSON.toJSONBytes(object,serializerFeatures);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return JSON.parseObject(new String(bytes, IOUtils.UTF8), Object.class, defaultRedisConfig);
        } catch (Exception ex) {
            throw new SerializationException("Could not deserialize: " + ex.getMessage(), ex);
        }
    }
}