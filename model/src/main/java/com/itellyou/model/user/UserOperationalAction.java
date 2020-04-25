package com.itellyou.model.user;

import com.itellyou.util.BaseEnum;

public enum UserOperationalAction implements BaseEnum<UserOperationalAction,Integer> {
    DEFAULT(0, "default"),
    FOLLOW(1, "follow"),
    LIKE(2, "like"),
    COMMENT(3, "comment"),
    PUBLISH(4, "publish");

    private Integer value;
    private String name;

    UserOperationalAction(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return getName();
    }

    public static UserOperationalAction valueOf(Integer value) {
        switch (value) {
            case 0:
                return DEFAULT;
            case 1:
                return FOLLOW;
            case 2:
                return LIKE;
            case 3:
                return COMMENT;
            case 4:
                return PUBLISH;
            default:
                return null;
        }
    }
}
