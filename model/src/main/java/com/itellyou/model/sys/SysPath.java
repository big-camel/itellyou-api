package com.itellyou.model.sys;

import com.itellyou.model.reward.RewardType;
import com.itellyou.util.BaseEnum;

public enum SysPath implements BaseEnum<RewardType,Integer> {
    RETAIN(0,"retain"),
    USER(1,"user"),
    COLUMN(2,"column");

    private int value;
    private String name;
    SysPath(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static SysPath valueOf(Integer value){
        switch (value){
            case 0:
                return RETAIN;
            case 1:
                return USER;
            case 2:
                return COLUMN;
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
