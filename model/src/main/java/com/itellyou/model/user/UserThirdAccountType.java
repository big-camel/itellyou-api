package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserThirdAccountType implements BaseEnum<UserThirdAccountType,Integer> {
    ALIPAY(1,"alipay"),
    WECHAT(2,"wechat"),
    GITHUB(3,"github");

    private int value;
    private String name;
    UserThirdAccountType(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static UserThirdAccountType valueOf(Integer value){
        switch (value){
            case 1:
                return ALIPAY;
            case 2:
                return WECHAT;
            case 3:
                return GITHUB;
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
