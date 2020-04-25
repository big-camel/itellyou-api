package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserPaymentStatus implements BaseEnum<UserPaymentStatus,Integer> {
    DEFAULT(0,"default"),
    SUCCEED(1,"succeed"),
    FAILED(2,"failed");

    private int value;
    private String name;
    UserPaymentStatus(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static UserPaymentStatus valueOf(Integer value){
        switch (value){
            case 0:
                return DEFAULT;
            case 1:
                return SUCCEED;
            case 2:
                return FAILED;
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
