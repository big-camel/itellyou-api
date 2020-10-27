package com.itellyou.service.sys;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeConfigModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysIncomeConfigSingleService {
    List<SysIncomeConfigModel> search(Collection<Long> ids,
                                      String name,
                                      Boolean isDeleted,
                                      Long userId,
                                      Long beginTime, Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);

    int count(Collection<Long> ids,
              String name,
              Boolean isDeleted,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysIncomeConfigModel> page(Collection<Long> ids,
                                         String name,
                                         Boolean isDeleted,
                                         Long userId,
                                         Long beginTime, Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);

}
