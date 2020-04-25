package com.itellyou.service.view;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.view.ViewInfoModel;

import java.util.List;
import java.util.Map;

public interface ViewInfoService {
    int insert(ViewInfoModel viewModel);

    List<ViewInfoModel> search(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String,String> order, Integer offset, Integer limit);

    List<ViewInfoModel> search(Long userId,Map<String,String> order,Integer offset,Integer limit);

    List<ViewInfoModel> search(Long userId,Integer offset,Integer limit);

    int count(Long id,Long userId,EntityType dataType,Long dataKey,String os,String browser,Long beginTime,Long endTime,Long ip);

    PageModel<ViewInfoModel> page(Long id,Long userId,EntityType dataType,Long dataKey,String os,String browser,Long beginTime,Long endTime,Long ip,Map<String,String> order,Integer offset,Integer limit);

    int update(ViewInfoModel viewModel);

    /**
     * 写入或更新浏览记录，返回最近一次浏览时间
     * @param userId
     * @param dataType
     * @param dataKey
     * @param ip
     * @param os
     * @param browser
     * @return
     */
    long insertOrUpdate(Long userId,EntityType dataType,Long dataKey,Long ip,String os,String browser) throws Exception;
}
