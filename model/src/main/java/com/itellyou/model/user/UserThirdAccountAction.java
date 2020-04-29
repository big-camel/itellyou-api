package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserThirdAccountAction implements BaseEnum<UserThirdAccountAction,Integer> {
    BIND(1,"bind"),
    LOGIN(2,"login");

    private int value;
    private String name;
    UserThirdAccountAction(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static UserThirdAccountAction valueOf(Integer value){
        switch (value){
            case 1:
                return BIND;
            case 2:
                return LOGIN;
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
