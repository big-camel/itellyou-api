package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserPaymentType implements BaseEnum<UserPaymentType,Integer> {
    ALIPAY(1,"alipay","支付宝"),
    WECHAT(2,"wechat","微信");

    private int value;
    private String name;
    private String text;
    UserPaymentType(int value, String name,String text){
        this.value = value;
        this.name = name;
        this.text = text;
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

    public String getText(){
        return this.text;
    }

    public String toString(){
        return getName();
    }
}
