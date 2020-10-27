package com.itellyou.model.sys;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class EntitySearchModel {
    private EntityType type;

    private Map<String,Object> args = new HashMap<>();

    public EntitySearchModel(EntityType type){
        this.type = type;
    }

    public <T> EntitySearchModel(EntityType type,String key,T value){
        this(type);
        args.put(key,value);
    }

    public EntitySearchModel addArgs(String key,Object value){
        this.args.put(key,value);
        return this;
    }
}
