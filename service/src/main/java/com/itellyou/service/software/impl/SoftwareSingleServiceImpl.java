package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareInfoDao;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_KEY)
@Service
public class SoftwareSingleServiceImpl implements SoftwareSingleService {

    private final SoftwareInfoDao infoDao;

    private final DataUpdateQueueService updateQueueService;

    public SoftwareSingleServiceImpl(SoftwareInfoDao infoDao, DataUpdateQueueService updateQueueService) {
        this.infoDao = infoDao;
        this.updateQueueService = updateQueueService;
    }

    @Override
    public SoftwareInfoModel findById(Long id) {
        List<SoftwareInfoModel> infoModels = search(new HashSet<Long>(){{ add(id);}},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        return infoModels != null && infoModels.size() > 0 ? infoModels.get(0) : null;
    }

    @Override
    public List<SoftwareInfoModel> search(Collection<Long> ids, String mode, Long columnId, Long userId, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SoftwareInfoModel> infoModels = RedisUtils.fetch(CacheKeys.SOFTWARE_KEY,SoftwareInfoModel.class,ids,(Collection<Long> fetchIds) ->
                infoDao.search(fetchIds,mode,columnId,userId,isDisabled,isPublished,isDeleted, minComment, maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
        );
        // 从缓存里面计算统计数据值
        List<DataUpdateStepModel> stepModels = updateQueueService.get(EntityType.SOFTWARE,infoModels.stream().map(SoftwareInfoModel::getId).collect(Collectors.toSet()));
        infoModels.forEach(model -> {
            stepModels.stream().filter(stepModel -> stepModel.getId().equals(model.getId())).findFirst().ifPresent(stepModel -> {
                model.setViewCount(model.getViewCount() + stepModel.getViewStep());
                model.setSupportCount(model.getSupportCount() + stepModel.getSupportStep());
                model.setOpposeCount(model.getOpposeCount() + stepModel.getOpposeStep());
                model.setCommentCount(model.getCommentCount() + stepModel.getCommentStep());
            });
        });
        return infoModels;
    }
}
