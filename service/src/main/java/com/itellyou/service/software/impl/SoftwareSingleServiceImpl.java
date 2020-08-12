package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareInfoDao;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "software")
@Service
public class SoftwareSingleServiceImpl implements SoftwareSingleService {

    private final SoftwareInfoDao infoDao;

    public SoftwareSingleServiceImpl(SoftwareInfoDao infoDao) {
        this.infoDao = infoDao;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public SoftwareInfoModel findById(Long id) {
        return infoDao.findById(id);
    }

    @Override
    public List<SoftwareInfoModel> search(HashSet<Long> ids, String mode, Long columnId, Long userId, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetchByCache("software",SoftwareInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                infoDao.search(fetchIds,mode,columnId,userId,isDisabled,isPublished,isDeleted, minComments, maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
        );
    }
}
