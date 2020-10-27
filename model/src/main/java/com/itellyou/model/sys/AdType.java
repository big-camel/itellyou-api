package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;
import com.itellyou.util.CacheEntity;

public enum AdType implements BaseEnum<EntityAction,Integer>, CacheEntity {
    BAIDU(1, "baidu"),
    ADSENSE(2, "adsense");

    private Integer value;
    private String name;

    AdType(Integer value, String name) {
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

    public static AdType valueOf(Integer value) {
        switch (value) {
            case 1:
                return BAIDU;
            case 2:
                return ADSENSE;
            default:
                return null;
        }
    }

    @Override
    public Object cacheKey() {
        return name;
    }
}
