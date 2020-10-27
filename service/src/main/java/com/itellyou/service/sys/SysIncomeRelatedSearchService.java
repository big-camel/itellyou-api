package com.itellyou.service.sys;

import com.itellyou.model.sys.SysIncomeRelatedDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysIncomeRelatedSearchService {

    List<SysIncomeRelatedDetailModel> search(Collection<Long> ids,
                                             Long incomeId,
                                             Long configId,
                                             Long userId,
                                             Long beginTime, Long endTime,
                                             Long ip,
                                             Map<String, String> order,
                                             Integer offset,
                                             Integer limit);
}
