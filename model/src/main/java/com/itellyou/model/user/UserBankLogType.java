package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserBankLogType implements BaseEnum<UserBankLogType,Integer> {
    DEFAULT(0,"default"),
    QUESTION_ASK(1,"question_ask"),
    QUESTION_ANSWER(2,"question_answer"),
    PAYMENT(3,"payment"),
    WITHDRAW(4,"withdraw"),
    FEE(5,"fee");

    private int value;
    private String name;
    UserBankLogType(int value,String name){
        this.value = value;
        this.name = name;
    }

    public static UserBankLogType valueOf(Integer value){
        switch (value){
            case 0:
                return DEFAULT;
            case 1:
                return QUESTION_ASK;
            case 2:
                return QUESTION_ANSWER;
            default:
                return null;
        }
    }
    @Override
    public Integer getValue() {
        return value;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return getName();
    }
}
