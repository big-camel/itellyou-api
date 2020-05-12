package com.itellyou.model.thirdparty;

import com.itellyou.util.BaseEnum;

public enum ThirdAccountType implements BaseEnum<ThirdAccountType,Integer> {
    ALIPAY(1,"alipay"),
    WECHAT(2,"wechat"),
    GITHUB(3,"github");

    private int value;
    private String name;
    ThirdAccountType(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static ThirdAccountType valueOf(Integer value){
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
