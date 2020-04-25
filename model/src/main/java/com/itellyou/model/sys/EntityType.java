package com.itellyou.model.sys;

import com.itellyou.util.BaseEnum;

public enum EntityType implements BaseEnum<EntityType,Integer> {
    USER(1, "user"),
    QUESTION(2, "question"),
    ANSWER(3, "answer"),
    ARTICLE(4, "article"),
    COLUMN(5, "column"),
    QUESTION_COMMENT(6, "question_comment"),
    ANSWER_COMMENT(7, "answer_comment"),
    ARTICLE_COMMENT(8, "article_comment"),
    TAG(9, "tag");

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
            default:
                return null;
        }
    }
}
