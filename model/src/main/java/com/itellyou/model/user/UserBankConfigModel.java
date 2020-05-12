package com.itellyou.model.user;

import com.itellyou.model.sys.CacheEntity;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBankConfigModel implements CacheEntity {
    private UserBankType bankType;
    private EntityAction action;
    private EntityType type;
    private int targeterStep;
    private int createrStep;
    private int createrMinScore;
    private int targeterCountOfDay;
    private int targeterTotalOfDay;
    private int targeterCountOfWeek;
    private int targeterTotalOfWeek;
    private int targeterCountOfMonth;
    private int targeterTotalOfMonth;
    private int createrCountOfDay;
    private int createrTotalOfDay;
    private int createrCountOfWeek;
    private int createrTotalOfWeek;
    private int createrCountOfMonth;
    private int createrTotalOfMonth;
    private String targeterRemark;
    private String createrRemark;
    private boolean onlyOnce;

    @Override
    public String cacheKey() {
        return bankType + "-" + action + "-" + type;
    }
}
