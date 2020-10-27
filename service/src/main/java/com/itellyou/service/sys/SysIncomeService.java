package com.itellyou.service.sys;

import com.itellyou.model.sys.SysIncomeModel;

import java.time.LocalDate;
import java.util.Map;

public interface SysIncomeService {

    int insertOrUpdate(SysIncomeModel model);

    boolean add(LocalDate date, Map<Long,Double> value, Long userId, Long ip);
}
