package com.itellyou.service.sys;

import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.CacheEntity;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface EntityService {

    <T extends CacheEntity> EntityDataModel<T> search(EntitySearchModel... searchModels);

    <T extends CacheEntity> EntityDataModel<T> search(EntityType type, Map<String,Object> args);

    <T extends CacheEntity> EntityDataModel<T> search(EntityType type, String key,Object value);

    <T extends CacheEntity> EntityDataModel<T> search(EntityType type);

    <T extends CacheEntity,E extends T> EntityDataModel<T> search(Collection<E> data, BiFunction<E,Function<EntityType,Map<String,Object>>, EntitySearchModel>... searchModelFunctions);
}
