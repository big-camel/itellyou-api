package com.itellyou.model.common;

import com.itellyou.model.sys.CacheEntity;
import com.itellyou.util.BaseEnum;

public enum NotificationDisplay implements BaseEnum<NotificationDisplay,Integer> , CacheEntity {
    NONE(0,"none"),
    NOTIFICATION_ONLY(1, "notification_only"),
    NOTIFICATION_EMAIL(2, "notification_email"),
    NOTIFICATION_MOBILE(3, "notification_mobile");

    private Integer value;
    private String name;

    NotificationDisplay(Integer value, String name) {
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

    public static NotificationDisplay valueOf(Integer value) {
        switch (value) {
            case 0:return NONE;
            case 1:
                return NOTIFICATION_ONLY;
            case 2:
                return NOTIFICATION_EMAIL;
            case 3:
                return NOTIFICATION_MOBILE;
            default:
                return null;
        }
    }

    @Override
    public String cacheKey() {
        return name;
    }
}
