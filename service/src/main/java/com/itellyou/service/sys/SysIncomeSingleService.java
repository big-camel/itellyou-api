package com.itellyou.service.sys;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysIncomeSingleService {

    List<SysIncomeModel> search(Collection<Long> ids,
                                Long userId,
                                Long beginDate, Long endDate,
                                Long beginTime, Long endTime,
                                Long ip,
                                Map<String, String> order,
                                Integer offset,
                                Integer limit);

    int count(Collection<Long> ids,
              Long userId,
              Long beginDate, Long endDate,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysIncomeModel> page(Collection<Long> ids,
                                     Long userId,
                                     Long beginDate, Long endDate,
                                     Long beginTime, Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);

    SysIncomeModel findByDate(Long date);

    SysIncomeModel find(Long id);
}
