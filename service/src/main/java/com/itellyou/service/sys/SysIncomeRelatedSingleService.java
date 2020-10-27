package com.itellyou.service.sys;

import com.itellyou.model.sys.SysIncomeRelatedModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysIncomeRelatedSingleService {

    List<SysIncomeRelatedModel> search(Collection<Long> ids,
                                       Long incomeId,
                                       Long configId,
                                       Long userId,
                                       Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order,
                                       Integer offset,
                                       Integer limit);

    int count(Collection<Long> ids,
              Long incomeId,
              Long configId,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

}
