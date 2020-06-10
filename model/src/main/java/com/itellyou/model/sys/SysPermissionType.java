package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;
import com.itellyou.util.CacheEntity;

public enum SysPermissionType implements BaseEnum<SysPermissionType,Integer> , CacheEntity {
    NEGOTIATED(0,"negotiated"),
    URL(1,"url"),
    BUTTON(2,"button");

    private int value;
    private String name;
    SysPermissionType(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static SysPermissionType valueOf(Integer value){
        switch (value){
            case 0:
                return NEGOTIATED;
            case 1:
                return URL;
            case 2:
                return BUTTON;
            default:
                return null;
        }
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return getName();
    }

    @Override
    public String cacheKey() {
        return name;
    }
}
