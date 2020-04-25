package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserPaymentType implements BaseEnum<UserPaymentType,Integer> {
    ALIPAY(1,"alipay"),
    WECHAT(2,"wechat");

    private int value;
    private String name;
    UserPaymentType(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static UserPaymentType valueOf(Integer value){
        switch (value){
            case 1:
                return ALIPAY;
            case 2:
                return WECHAT;
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
