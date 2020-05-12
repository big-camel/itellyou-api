package com.itellyou.model.sys;

public interface CacheEntity {
    /**
     * 缓存的实体类实现本接口的cacheKey方法，可以指定缓存时 redis key的唯一后缀restart.include.cache=/spring-data-redis-.*.jar
     */
    String cacheKey();
}