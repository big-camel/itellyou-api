package com.itellyou.model.article;

import com.itellyou.util.BaseEnum;

public enum ArticleSourceType implements BaseEnum<ArticleSourceType,Integer> {
    ORIGINAL(1,"original"),
    TRANSLATION(2,"translation"),
    REPRODUCED(3,"reproduced");

    private Integer value;
    private String name;
    ArticleSourceType(Integer value,String name){
        this.value = value;
        this.name = name;
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

    public static ArticleSourceType valueOf(Integer value){
        switch (value){
            case 1:
                return ORIGINAL;
            case 2:
                return TRANSLATION;
            case 3:
                return REPRODUCED;
            default:
                return null;
        }
    }
}
