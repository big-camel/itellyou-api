package com.itellyou.api.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.itellyou.api.serializer.GenericFastJsonRedisSerializer;
import com.itellyou.util.CacheKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new FastJsonRedisSerializer<>(String.class));
        template.setValueSerializer(new GenericFastJsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * redis缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(this.cacheConfiguration(Duration.ofMinutes(2)))
                .withInitialCacheConfigurations(this.cacheConfigurationMap())
                .build();
        return cacheManager;
    }

    /**
     * redis缓存管理器配置列表；
     * 可以根据业务需要配置不同的过期时间；
     */
    private Map<String, RedisCacheConfiguration> cacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        //登录的token
        configurationMap.put("login_token", this.cacheConfiguration(Duration.ofDays(1)));
        //用户信息
        configurationMap.put("user_info", this.cacheConfiguration(Duration.ofDays(1)));
        //用户银行信息
        configurationMap.put("bank_info",this.cacheConfiguration(Duration.ofDays(1)));
        //用户的第三方账户
        configurationMap.put("user_third_account",this.cacheConfiguration(Duration.ofDays(1)));
        //阿里云配置
        configurationMap.put("ali_config",this.cacheConfiguration(Duration.ofDays(1)));
        //阿里云邮件配置
        configurationMap.put("ali_dm_config",this.cacheConfiguration(Duration.ofDays(1)));
        //阿里云短信配置
        configurationMap.put("ali_sms_config",this.cacheConfiguration(Duration.ofDays(1)));
        //支付宝配置
        configurationMap.put("alipay_config",this.cacheConfiguration(Duration.ofDays(1)));
        //Github配置
        configurationMap.put("github_config",this.cacheConfiguration(Duration.ofDays(1)));
        //Geetest 验证配置
        configurationMap.put("geetest_config",this.cacheConfiguration(Duration.ofDays(1)));
        //path
        configurationMap.put("sys_path",this.cacheConfiguration(Duration.ofDays(1)));
        //reward_config
        configurationMap.put("reward_config",this.cacheConfiguration(Duration.ofDays(1)));
        //credit_config
        configurationMap.put("credit_config",this.cacheConfiguration(Duration.ofDays(1)));
        //score_config
        configurationMap.put("score_config",this.cacheConfiguration(Duration.ofDays(1)));
        //消息显示配置
        configurationMap.put("notification_display",this.cacheConfiguration(Duration.ofDays(1)));
        //sys_permission
        configurationMap.put("sys_permission",this.cacheConfiguration(Duration.ofDays(1)));
        //sys_role_permission
        configurationMap.put("sys_role_permission",this.cacheConfiguration(Duration.ofDays(1)));
        //sys_role
        configurationMap.put("sys_role",this.cacheConfiguration(Duration.ofDays(1)));
        //user_rank_role
        configurationMap.put("user_rank_role",this.cacheConfiguration(Duration.ofDays(1)));
        //user_rank
        configurationMap.put("user_rank",this.cacheConfiguration(Duration.ofDays(1)));
        //user_role
        configurationMap.put("user_role",this.cacheConfiguration(Duration.ofDays(1)));
        return configurationMap;
    }


    /**
     * redis缓存管理器的默认配置；
     * 使用fastJson序列化value,model不再需要实现Serializable接口；
     *
     * @param ttl 设置默认的过期时间，防止 redis 内存泄漏
     */
    private RedisCacheConfiguration cacheConfiguration(Duration ttl) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(String.class)))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()))
                .entryTtl(ttl);
        return configuration;
    }


    /**
     * 通用redis缓存key生成策略
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuffer sb = new StringBuffer();
            for (Object obj : params) {
                sb.append(CacheKeyUtil.getCacheKey(obj));
            }
            logger.info("cacheKey={}", sb.toString());
            return sb.toString();
        };
    }
}