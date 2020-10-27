package com.itellyou.service.common;

import com.itellyou.model.common.ViewDetailModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;

import java.util.List;
import java.util.Map;

public interface ViewSearchService {
    List<ViewDetailModel> search(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String,String> order, Integer offset, Integer limit);

    List<ViewDetailModel> search(Long userId,Map<String,String> order,Integer offset,Integer limit);

    List<ViewDetailModel> search(Long userId,Integer offset,Integer limit);

    int count(Long id,Long userId,EntityType dataType,Long dataKey,String os,String browser,Long beginTime,Long endTime,Long ip);

    PageModel<ViewDetailModel> page(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String,String> order, Integer offset, Integer limit);

}
