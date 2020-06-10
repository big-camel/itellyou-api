package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;
import com.itellyou.util.CacheEntity;

public enum EntityAction implements BaseEnum<EntityAction,Integer> , CacheEntity {
    DEFAULT(0, "default"),
    FOLLOW(1, "follow"),
    LIKE(2, "like"),
    COMMENT(3, "comment"),
    PUBLISH(4, "publish"),
    DELETE(5, "delete"),
    UNFOLLOW(6, "unfollow"),
    UNLIKE(7, "unlike"),
    VIEW(8, "view"),
    REVERT(9, "revert"),
    UPDATE(10, "update"),
    ADOPT(11, "adopt"),
    DISLIKE(12, "dislike"),
    UNDISLIKE(13, "undislike"),
    PAYMENT(14,"payment"),
    WITHDRAW(15,"withdraw"),
    BIND(16,"bind"),
    UNBIND(17,"unbind"),
    REWARD(18,"reward");

    private Integer value;
    private String name;

    EntityAction(Integer value, String name) {
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

    public static EntityAction valueOf(Integer value) {
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
            case 5:
                return DELETE;
            case 6:
                return UNFOLLOW;
            case 7:
                return UNLIKE;
            case 8:
                return VIEW;
            case 9:
                return REVERT;
            case 10:
                return UPDATE;
            case 11:
                return ADOPT;
            case 12:
                return DISLIKE;
            case 13:
                return UNDISLIKE;
            case 14:
                return PAYMENT;
            case 15:
                return WITHDRAW;
            case 16:
                return BIND;
            case 17:
                return UNBIND;
            case 18:
                return REWARD;
            default:
                return null;
        }
    }

    @Override
    public String cacheKey() {
        return name;
    }
}
