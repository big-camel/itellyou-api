package com.itellyou.service.sys;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdSlotModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysAdSlotSingleService {

    List<SysAdSlotModel> search(Collection<Long> ids,
                                String name,
                                Collection<Long> adIds,
                                Long userId,
                                Long beginTime, Long endTime,
                                Long ip,
                                Map<String, String> order,
                                Integer offset,
                                Integer limit);

    int count(Collection<Long> ids,
              String name,
              Collection<Long> adIds,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysAdSlotModel> page(Collection<Long> ids,
                                     String name,
                                   Collection<Long> adIds,
                                     Long userId,
                                     Long beginTime, Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);

    List<SysAdSlotModel> findByAdId(Long adId);

}
