package com.itellyou.model.thirdparty;

import com.itellyou.util.BaseEnum;

public enum GeetestClientTypeEnum implements BaseEnum<GeetestClientTypeEnum, String> {
    WEB("web") ,
    H5("h5"),
    NATIVE("native");

    private String value;

    GeetestClientTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
