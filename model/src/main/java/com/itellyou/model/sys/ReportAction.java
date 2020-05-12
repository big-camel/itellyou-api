package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;

public enum ReportAction implements BaseEnum<ReportAction,Integer> , CacheEntity {
    PORN(1, "porn"),
    ANTISOCIAL(2, "antisocial"),
    CRIME(3, "crime"),
    OTHER(4, "other");

    private Integer value;
    private String name;

    ReportAction(Integer value, String name) {
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

    public static ReportAction valueOf(Integer value) {
        switch (value) {
            case 1:
                return PORN;
            case 2:
                return ANTISOCIAL;
            case 3:
                return CRIME;
            case 4:
                return OTHER;
            default:
                return null;
        }
    }

    @Override
    public String cacheKey() {
        return name;
    }
}
