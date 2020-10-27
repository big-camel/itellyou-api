package com.itellyou.service.common;

import com.itellyou.model.common.StarModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StarService<T extends StarModel> {

    int insert(T model) throws Exception;

    int delete(Long targetId, Long userId,Long ip) throws Exception;

    List<? extends T> search(Collection<Long> targetIds, Long userId,
                             Long beginTime, Long endTime,
                             Long ip,
                             Map<String, String> order,
                             Integer offset,
                             Integer limit);
    int count(Collection<Long> targetIds, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<? extends T> page(Collection<Long> targetIds, Long userId,
                                           Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);
}
