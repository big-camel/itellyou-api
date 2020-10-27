package com.itellyou.service.sys;

import com.itellyou.model.sys.AdType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysAdSingleService {

    List<SysAdModel> search(Collection<Long> ids,
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

    int count(Collection<Long> ids,
              AdType type,
              String name,
              Boolean enabledForeign,
              Boolean enabledCn,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysAdModel> page(Collection<Long> ids,
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
}
