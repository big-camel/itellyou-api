package com.itellyou.api.config;

import com.itellyou.api.serializer.GenericFastJsonRedisSerializer;
import com.itellyou.model.constant.CacheKeys;
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
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
        template.setKeySerializer(new StringRedisSerializer());
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
                .cacheDefaults(this.cacheConfiguration(Duration.ofHours(1)))
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
        configurationMap.put(CacheKeys.LOGIN_TOKEN_KEY, this.cacheConfiguration(Duration.ofDays(1)));
        //用户信息
        configurationMap.put(CacheKeys.USER_INFO_KEY, this.cacheConfiguration(Duration.ofDays(1)));
        //用户银行信息
        configurationMap.put(CacheKeys.BANK_KEY,this.cacheConfiguration(Duration.ofDays(1)));
        //用户的第三方账户
        configurationMap.put(CacheKeys.USER_THIRD_ACCOUNT_KEY,this.cacheConfiguration(Duration.ofDays(1)));
        //阿里云配置
        configurationMap.put(CacheKeys.ALI_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //阿里云邮件配置
        configurationMap.put(CacheKeys.ALI_DM_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //阿里云短信配置
        configurationMap.put(CacheKeys.ALI_SMS_CONFIG,this.cacheConfiguration(Duration.ofDays(30)));
        //支付宝配置
        configurationMap.put(CacheKeys.ALIPAY_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //Github配置
        configurationMap.put(CacheKeys.GITHUB_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //Geetest 验证配置
        configurationMap.put(CacheKeys.GEETEST_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //path
        configurationMap.put(CacheKeys.SYS_PATH_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //reward_config
        configurationMap.put(CacheKeys.REWARD_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //credit_config
        configurationMap.put(CacheKeys.CREDIT_CONFIG_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //消息显示配置
        configurationMap.put(CacheKeys.NOTIFICATION_DISPLAY_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //sys_permission
        configurationMap.put(CacheKeys.SYS_PERMISSION_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //sys_role_permission
        configurationMap.put(CacheKeys.SYS_ROLE_PERMISSION_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //sys_role
        configurationMap.put(CacheKeys.SYS_ROLE_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //user_rank_role
        configurationMap.put(CacheKeys.USER_RANK_ROLE_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //user_rank
        configurationMap.put(CacheKeys.USER_RANK_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //user_role
        configurationMap.put(CacheKeys.USER_ROLE_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //sys_link
        configurationMap.put(CacheKeys.SYS_LINK_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //sys_setting
        configurationMap.put(CacheKeys.SYS_SETTING_KEY,this.cacheConfiguration(Duration.ofDays(30)));
        //statistics_queue
        configurationMap.put(CacheKeys.DATAUPDATE_QUEUE_KEY,this.cacheConfiguration(Duration.ofDays(360)));
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
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
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