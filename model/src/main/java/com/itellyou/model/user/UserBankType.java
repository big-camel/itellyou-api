package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;
import com.itellyou.model.reward.RewardType;

public enum UserBankType implements BaseEnum<UserBankType,Integer> {
    CREDIT(1,"credit"),
    CASH(2,"cash");

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
}
