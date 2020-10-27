package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;
import com.itellyou.util.CacheEntity;

public enum EntityType implements BaseEnum<EntityType,Integer> , CacheEntity {
    DEFAULT(0, "default"),
    USER(1, "user"),
    QUESTION(2, "question"),
    ANSWER(3, "answer"),
    ARTICLE(4, "article"),
    COLUMN(5, "column"),
    QUESTION_COMMENT(6, "question_comment"),
    ANSWER_COMMENT(7, "answer_comment"),
    ARTICLE_COMMENT(8, "article_comment"),
    TAG(9, "tag"),
    PAYMENT(10,"payment"),
    WITHDRAW(11,"withdraw"),
    GITHUB(12,"github"),
    ALIPAY(13,"alipay"),
    MOBILE(14,"mobile"),
    EMAIL(15,"email"),
    FEE(16,"fee"),
    SOFTWARE(17,"software"),
    SOFTWARE_COMMENT(18, "software_comment"),
    SYSTEM(19, "system");

    private Integer value;
    private String name;

    EntityType(Integer value, String name) {
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

    public static EntityType get(String value) {
        try{
            return valueOf(value.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }

    public static EntityType valueOf(Integer value) {
        switch (value) {
            case 1:
                return USER;
            case 2:
                return QUESTION;
            case 3:
                return ANSWER;
            case 4:
                return ARTICLE;
            case 5:
                return COLUMN;
            case 6:
                return QUESTION_COMMENT;
            case 7:
                return ANSWER_COMMENT;
            case 8:
                return ARTICLE_COMMENT;
            case 9:
                return TAG;
            case 10:
                return PAYMENT;
            case 11:
                return WITHDRAW;
            case 12:
                return GITHUB;
            case 13:
                return ALIPAY;
            case 14:
                return MOBILE;
            case 15:
                return EMAIL;
            case 16:
                return FEE;
            case 17:
                return SOFTWARE;
            case 18:
                return SOFTWARE_COMMENT;
            default:
                return null;
        }
    }

    @Override
    public String cacheKey() {
        return name;
    }
}
