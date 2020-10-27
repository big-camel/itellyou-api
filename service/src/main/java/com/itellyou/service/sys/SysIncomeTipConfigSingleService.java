package com.itellyou.service.sys;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeTipConfigModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysIncomeTipConfigSingleService {

    List<SysIncomeTipConfigModel> search(Collection<Long> ids,
                                   String name,
                                   EntityType dataType,
                                   Long userId,
                                   Long beginTime, Long endTime,
                                   Long ip,
                                   Map<String, String> order,
                                   Integer offset,
                                   Integer limit);

    int count(Collection<Long> ids,
              String name,
              EntityType dataType,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysIncomeTipConfigModel> page(Collection<Long> ids,
                                        String name,
                                        EntityType dataType,
                                        Long userId,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);
}
