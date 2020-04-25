package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;

public enum VoteType implements BaseEnum<VoteType,Integer> {
    SUPPORT(1,"support"),
    OPPOSE(0,"oppose");

    private Integer value;
    private String name;
    VoteType(Integer value,String name){
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

    public String toString(){
        return getName();
    }

    public static VoteType valueOf(Integer value){
        switch (value){
            case 0:
                return OPPOSE;
            case 1:
                return SUPPORT;
            default:
                return null;
        }
    }
}
