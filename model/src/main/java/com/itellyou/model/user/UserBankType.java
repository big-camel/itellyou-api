package com.itellyou.model.user;

import com.itellyou.util.CacheEntity;
import com.itellyou.util.BaseEnum;

public enum UserBankType implements BaseEnum<UserBankType,Integer> , CacheEntity {
    CREDIT(1,"credit"),
    CASH(2,"cash"),
    SCORE(3,"score");

    private int value;
    private String name;
    UserBankType(int value,String name){
        this.value = value;
        this.name = name;
    }

    public static UserBankType valueOf(Integer value){
        switch (value){
            case 1:
                return CREDIT;
            case 2:
                return CASH;
            case 3:
                return SCORE;
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
