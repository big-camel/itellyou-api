package com.itellyou.util;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static <T> T get(String name,Object key,Class<T> clazz){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        return cache.get(key.toString(),clazz);
    }

    public static void set(String name,Object key,Object value){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        cache.put(key.toString(),value);
    }

    public static void remove(String name,Object key){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        cache.evict(key.toString());
    }

    public static void clear(String name){
        RedisCache cache = (RedisCache)cacheManager.getCache(name);
        cache.clear();
    }

    public static <T,E,K> Map<K,List<T>> fetch(String name, Collection<E> ids, Function<Collection<E>,List<T>> callback, Function<E, K> getIdKey, Function<T, K> getEntityKey) {
        Map<K,List<T>> infoModels = new LinkedHashMap<>();
        List<T> cacheModels = new LinkedList<>();
        List<T> fetchModels = new LinkedList<>();
        Collection<E> fetchIds = new LinkedHashSet<>();

        if(ids != null && ids.size() > 0){
            for (E id : ids){
                Object cacheKey = getIdKey.apply(id);
                Collection<T> cacheData = get(name,cacheKey,List.class);
                if(cacheData != null){
                    cacheData.forEach((T data) -> cacheModels.add(data));
                }else{
                    fetchIds.add(id);
                }
            }
            if(fetchIds.size() > 0){
                fetchModels = callback.apply(fetchIds);
            }

            for (E id : ids){
                Object cacheKey = getIdKey.apply(id);
                List<T> cacheList = cacheModels.stream().filter(model -> getEntityKey.apply(model).toString().equals(cacheKey.toString())).collect(Collectors.toList());
                List<T> fetchList = fetchModels.stream().filter(model -> getEntityKey.apply(model).toString().equals(cacheKey.toString())).collect(Collectors.toList());

                if(cacheList.size() > 0 || fetchList.size() > 0){
                    List<T> list = infoModels.computeIfAbsent((K)cacheKey,(key) -> new LinkedList<>());
                    list.addAll(cacheList);
                    list.addAll(fetchList);
                    set(name,cacheKey,list);
                }
            }
        }else{
            callback.apply(null).forEach((T model) -> {
                infoModels.computeIfAbsent((K)getEntityKey.apply(model),(key) -> new LinkedList()).add(model);
            });
            infoModels.keySet().forEach(key -> set(name,key,infoModels.get(key)));
        }
        return infoModels;
    }

    /**
     * 从缓存查询数据
     * @param name 缓存名称
     * @param clazz 存储类型
     * @param ids key集合，不传key集合，将查询的数据全部缓存
     * @param callback 未从缓存查询到的数据回调，传回未查询到的key集合，并需要返回这些key集合的数据，这些数据将被缓存
     * @param getIdKey 获取缓存Key
     * @param getEntityKey 获取缓存Key
     * @param <T> 返回类型
     * @param <E> key类型
     * @return List<T>
     */
    public static <T,E> List<T> fetch(String name, Class<T> clazz, Collection<E> ids, Function<Collection<E>,List<T>> callback, Function<E, Object> getIdKey, Function<T, Object> getEntityKey){
        List<T> infoModels = new LinkedList<>();
        Map<String,T> cacheModels = new LinkedHashMap<>();
        Map<String,T> fetchModels = new LinkedHashMap<>();
        LinkedHashSet<E> fetchIds = new LinkedHashSet<>();

        if(ids != null && ids.size() > 0){
            for (E id : ids){
                Object cacheKey = getIdKey.apply(id);
                T cacheData = get(name,cacheKey,clazz);
                if(cacheData != null){
                    cacheModels.put(cacheKey.toString(),cacheData);
                }else{
                    fetchIds.add(id);
                }
            }
            //未查询到缓存数据，去查询
            if(fetchIds.size() > 0){
                fetchModels = callback.apply(fetchIds).stream().collect(Collectors.toMap(model -> getEntityKey.apply(model).toString(),model -> model));
            }

            for (E id : ids){
                Object cacheKey = getIdKey.apply(id);
                if(cacheModels.containsKey(cacheKey.toString())){
                    infoModels.add(cacheModels.get(cacheKey.toString()));
                }
                else if(fetchModels.containsKey(cacheKey.toString())){
                    T fetchModel = fetchModels.get(cacheKey.toString());
                    infoModels.add(fetchModel);
                    set(name,cacheKey,fetchModel);
                }
            }
        }else{
            infoModels = callback.apply(null);
            infoModels.forEach(fetchModel -> {
                set(name,getEntityKey.apply(fetchModel),fetchModel);
            });
        }
        return infoModels;
    }

    public static <T,K,V> List<T> fetch(String name, Class<T> clazz, Map<K,V> map, Function<Map<K,V>,List<T>> callback, BiFunction<K,V, Object> getIdKey, Function<T, Object> getEntityKey){
        List<T> infoModels = new LinkedList<>();
        Map<String,T> cacheModels = new LinkedHashMap<>();
        Map<String,T> fetchModels = new LinkedHashMap<>();
        Map<K,V> fetchMap = new LinkedHashMap<>();

        if(map != null && map.size() > 0){
            map.forEach((key,value) -> {
                Object cacheKey = getIdKey.apply(key,value);
                T cacheData = get(name,cacheKey,clazz);
                if(cacheData != null){
                    cacheModels.put(cacheKey.toString(),cacheData);
                }else{
                    fetchMap.put(key,value);
                }
            });
            //未查询到缓存数据，去查询
            if(fetchMap.size() > 0){
                fetchModels = callback.apply(fetchMap).stream().collect(Collectors.toMap(model -> getEntityKey.apply(model).toString(),model -> model));
            }
            for (K key : map.keySet()){
                Object cacheKey = getIdKey.apply(key,map.get(key));
                if(cacheModels.containsKey(cacheKey.toString())){
                    infoModels.add(cacheModels.get(cacheKey.toString()));
                }
                else if(fetchModels.containsKey(cacheKey.toString())){
                    T fetchModel = fetchModels.get(cacheKey.toString());
                    infoModels.add(fetchModel);
                    set(name,cacheKey,fetchModel);
                }
            }
        }else{
            infoModels = callback.apply(null);
            infoModels.forEach(fetchModel -> set(name,getEntityKey.apply(fetchModel),fetchModel));
        }
        return infoModels;
    }

    /**
     * 从缓存查询数据
     * @param name 缓存名称
     * @param clazz 存储类型
     * @param ids key集合，不传key集合，将查询的数据全部缓存
     * @param callback 未从缓存查询到的数据回调，传回未查询到的key集合，并需要返回这些key集合的数据，这些数据将被缓存
     * @param <T> 返回类型
     * @param <E> key类型
     * @return List<T> 返回数据集合，通过 CacheEntity 的 cacheKey 来比较key集合，以保持数据排序一致
     */
    public static <T extends CacheEntity,E> List<T> fetch(String name, Class<T> clazz, Collection<E> ids, Function<Collection<E>,List<T>> callback){
        return fetch(name,clazz,ids,callback,model -> model.cacheKey());
    }

    public static <T extends CacheEntity,E> List<T> fetch(String name, Class<T> clazz, Collection<E> ids, Function<Collection<E>,List<T>> callback, Function<T, Object> getEntityKey){
        return fetch(name,clazz,ids,callback,id -> id,getEntityKey);
    }

    public static <T extends CacheEntity,E,K> Map<K,List<T>> fetch(String name, Collection<E> ids, Function<Collection<E>,List<T>> callback){
        return fetch(name,ids,callback,model -> (K)model.cacheKey());
    }

    public static <T extends CacheEntity,E,K> Map<K,List<T>> fetch(String name, Collection<E> ids, Function<Collection<E>,List<T>> callback, Function<T, K> getEntityKey){
        return fetch(name,ids,callback,id -> (K)id,getEntityKey);
    }

    public static <T extends CacheEntity,K , V> List<T> fetch(String name, Class<T> clazz, Map<K,V> map, Function<Map<K,V>,List<T>> callback){
        return fetch(name,clazz,map,callback,model -> model.cacheKey());
    }

    public static <T extends CacheEntity,K , V> List<T> fetch(String name, Class<T> clazz, Map<K,V> map, Function<Map<K,V>,List<T>> callback, Function<T, Object> getEntityKey) {
        return fetch(name, clazz, map, callback, (key, value) -> key + "-" + value, getEntityKey);
    }

    /**
     * 获取 Key集合
     * @param models 实体类集合
     * @param callback 回调函数，传入一个实体类，一个已经获取到的Key集合，并且返回新的Key集合
     * @param <T> 实体类类型
     * @param <E> Key值类型
     * @return Key集合
     */
    public static <T,E> Collection<E> getKeys(List<T> models, BiFunction<T,Collection<E>,Collection<E>> callback){
        Collection<E> ids = new LinkedHashSet<>();
        for (T model : models) {
            ids = callback.apply(model,ids);
        }
        return ids;
    }

    public static <T,E> Collection<E> getKeys(List<T> models, Function<T,E> callback){
        return getKeys(models,(T model,Collection<E> ids) -> {
            E key = callback.apply(model);
            if(!ids.contains(key)) ids.add(key);
            return ids;
        });
    }

    public static <T extends CacheEntity<E>,E> Collection<E> getKeys(List<T> models){
        return getKeys(models,(T model) -> model.cacheKey());
    }

    public static <T,K,E> HashMap<K,Collection<E>> getGroupKeys(List<T> models,Function<T,K> keyCallback,Function<T,E> valueCallback){
        HashMap<K,Collection<E>> data = new LinkedHashMap<>();
        for (T model : models) {
            K key = keyCallback.apply(model);
            if(!data.containsKey(key))
                data.put(key,new LinkedHashSet<>());
            data.get(key).add(valueCallback.apply(model));
        }
        return data;
    }

    public static <T extends CacheEntity<E>,K,E> HashMap<K,Collection<E>> getGroupKeys(List<T> models,Function<T,K> callback){
        return getGroupKeys(models,callback,(T model) -> model.cacheKey());
    }
}
