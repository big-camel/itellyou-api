package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;

public enum SysPermissionMethod implements BaseEnum<SysPermissionMethod,Integer> , CacheEntity {
    NEGOTIATED(0,"negotiated"),
    GET(1,"get"),
    POST(2,"post"),
    PUT(3,"put"),
    DELETE(4,"delete"),
    OPTIONS(5,"options"),
    CLICK(9,"click");

    private int value;
    private String name;
    SysPermissionMethod(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static SysPermissionMethod valueOf(Integer value){
        switch (value){
            case 0:
                return NEGOTIATED;
            case 1:
                return GET;
            case 2:
                return POST;
            case 3:
                return PUT;
            case 4:
                return DELETE;
            case 5:
                return OPTIONS;
            case 9:
                return CLICK;
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
