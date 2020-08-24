package com.itellyou.util;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class RedisUtils {

    private final CacheManager _cacheManager;

    private static CacheManager cacheManager;

    public RedisUtils(CacheManager _cacheManager) {
        this._cacheManager = _cacheManager;
    }

    @PostConstruct
    public void init() {
        cacheManager = _cacheManager;
    }

    public static <T> T getCache(String name,Object key,Class<T> clazz){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        return cache.get(key,clazz);
    }

    public static void setCache(String name,Object key,Object value){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        cache.put(key,value);
    }

    public static void removeCache(String name,Object key){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        cache.evict(key);
    }

    public static void clear(String name){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        cache.clear();
    }

    public static <T extends CacheEntity> List<T> fetchByCache(String name, Class<T> clazz, HashSet<Long> ids, Function<HashSet<Long>,List<T>> callback, BiFunction<T, Long, Boolean> checkFun){
        List<T> infoModels = new ArrayList<>();
        List<T> cacheModels = new LinkedList<>();
        List<T> fetchModels = new LinkedList<>();
        HashSet<Long> fetchIds = null;
        if(ids != null && ids.size() > 0){
            fetchIds = new LinkedHashSet<>();
            for (Long id : ids){
                T cacheData = getCache(name,id,clazz);
                if(cacheData != null && checkFun.apply(cacheData,id)){
                    cacheModels.add(cacheData);
                }else{
                    fetchIds.add(id);
                }
            }
            if(fetchIds.size() > 0){
                fetchModels = callback.apply(fetchIds);
            }
            for (Long id : ids){
                boolean isFind = false;
                for (T cacheMode : cacheModels ){
                    if(checkFun.apply(cacheMode,id)){
                        infoModels.add(cacheMode);
                        isFind = true;
                        break;
                    }
                }
                if(isFind) continue;
                for (T fetchModel : fetchModels ){
                    if(checkFun.apply(fetchModel,id)){
                        infoModels.add(fetchModel);
                        setCache(name,id,fetchModel);
                        break;
                    }
                }
            }
        }else{
            infoModels = callback.apply(null);
            for (T fetchModel : infoModels ){
                if(StringUtils.isEmpty(fetchModel.cacheKey())) continue;
                setCache(name,fetchModel.cacheKey(),fetchModel);
            }
        }
        return infoModels;
    }

    public static <T extends CacheEntity> List<T> fetchByCache(String name, Class<T> clazz, HashSet<Long> ids, Function<HashSet<Long>,List<T>> callback){
        return fetchByCache(name,clazz,ids,callback,(T obj,Long id) -> id != null && obj.cacheKey().equals(id.toString()));
    }
}
