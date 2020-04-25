package com.itellyou.model.upload;

import com.itellyou.util.BaseEnum;

public enum UploadType implements BaseEnum<UploadType,String> {
    IMAGE("image","image"),
    FILE("file","file"),
    DOC("doc","doc"),
    VIDEO("video","video");

    private String value;
    private String name;
    UploadType(String value, String name){
        this.value = value;
        this.name = name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return getName();
    }
}
