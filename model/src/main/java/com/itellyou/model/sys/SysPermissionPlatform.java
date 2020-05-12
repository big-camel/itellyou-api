package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;

public enum SysPermissionPlatform implements BaseEnum<SysPermissionPlatform,Integer> , CacheEntity {
    WEB(1,"web"),
    API(2,"api"),
    ADMIN(3,"admin");

    private int value;
    private String name;
    SysPermissionPlatform(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static SysPermissionPlatform valueOf(Integer value){
        switch (value){
            case 1:
                return WEB;
            case 2:
                return API;
            case 3:
                return ADMIN;
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
