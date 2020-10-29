package com.itellyou.service.sys;

import com.itellyou.model.sys.AdType;
import com.itellyou.model.sys.SysAdDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysAdSearchService {

    List<SysAdDetailModel> search(Collection<Long> ids,
                                  AdType type,
                                  String name,
                                  Boolean enabledForeign,
                                  Boolean enabledCn,
                                  Long userId,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);

    SysAdDetailModel findByEnabledForeign(Boolean enabledForeign);

    SysAdDetailModel findByEnabledCn(Boolean enabledCn);
}
