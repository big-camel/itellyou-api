package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;

public enum RewardType implements BaseEnum<RewardType,Integer> , CacheEntity {
    DEFAULT(0,"default"),
    CREDIT(1,"credit"),
    CASH(2,"cash");

    private int value;
    private String name;
    RewardType(int value,String name){
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public String getName(){
        return this.name;
    }

    public static RewardType valueOf(Integer value){
        switch (value){
            case 0:
                return DEFAULT;
            case 1:
                return CREDIT;
            case 2:
                return CASH;
            default:
                return null;
        }
    }

    public String toString(){
        return getName();
    }

    @Override
    public String cacheKey() {
        return name;
    }
}
