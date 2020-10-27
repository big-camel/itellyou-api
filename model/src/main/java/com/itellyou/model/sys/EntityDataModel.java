package com.itellyou.model.sys;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class EntityDataModel<T extends CacheEntity> {

    private Map<EntityType, Collection<T>> data;

    public <V extends T> Collection<V> get(EntityType type) {
        return data.containsKey(type) ? (Collection<V>)data.get(type) : null;
    }

    public <K,V extends T> Map<K,V> toMap(EntityType type, Function<V,K> callback){
        Collection<V> list = get(type);
        return list != null ? list.stream().collect(Collectors.toMap(callback::apply, model -> model)) : new HashMap<>();
    }

    public <K,V extends T> Map<K,V> toMap(EntityType type){
        return toMap(type,model -> (K)model.cacheKey());
    }

    public <K,V extends T> V get(EntityType type,K key,Function<V,K> callback){
        if(key == null) return null;
        return toMap(type,callback).get(key);
    }

    public <K,V extends T> V get(EntityType type,K key){
        if(key == null) return null;
        Map<K,V> map = toMap(type);
        return map.get(key);
    }
}
