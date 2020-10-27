package com.itellyou.service.sys.impl;

import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.sys.EntitySearchService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class EntityServiceImpl implements EntityService {

    @Override
    public <T extends CacheEntity> EntityDataModel<T> search(EntitySearchModel... searchModels) {
        Map<EntityType, Collection<T>> data = new HashMap<>();
        for (EntitySearchModel searchModel : searchModels){
            EntitySearchService searchService = EntitySearchFactory.getInstance().create(searchModel.getType());
            if(searchService != null){
                Collection<T> list = searchService.search(searchModel.getArgs());
                data.put(searchModel.getType(),list);
            }
        }
        return new EntityDataModel<>(data);
    }

    @Override
    public <T extends CacheEntity> EntityDataModel<T> search(EntityType type, Map<String, Object> args) {
        return search(new EntitySearchModel(type,args));
    }

    @Override
    public <T extends CacheEntity> EntityDataModel<T> search(EntityType type, String key, Object value) {
        return search(new EntitySearchModel(type,key,value));
    }

    @Override
    public <T extends CacheEntity> EntityDataModel<T> search(EntityType type) {
        return search(new EntitySearchModel(type));
    }

    @Override
    public <T extends CacheEntity,E extends T> EntityDataModel<T> search(Collection<E> data, BiFunction<E, Function<EntityType, Map<String, Object>>, EntitySearchModel>... searchModelFunctions) {
        Map<EntityType,EntitySearchModel> searchModelMap = new HashMap<>();
        Function<EntityType,Map<String,Object>> getArgs = type -> searchModelMap.computeIfAbsent(type,key -> new EntitySearchModel(key)).getArgs();
        data.forEach(model -> {
            for (BiFunction<E, Function<EntityType, Map<String, Object>>, EntitySearchModel> function : searchModelFunctions) {
                EntitySearchModel searchModel = function.apply(model,getArgs);
                searchModelMap.put(searchModel.getType(),searchModel);
            }
        });
        return search(searchModelMap.values().toArray(new EntitySearchModel[searchModelMap.size()]));
    }
}
