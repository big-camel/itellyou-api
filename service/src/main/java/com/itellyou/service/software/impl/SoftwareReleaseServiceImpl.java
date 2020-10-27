package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareReleaseDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareReleaseDetailModel;
import com.itellyou.model.software.SoftwareReleaseModel;
import com.itellyou.model.software.SoftwareUpdaterDetailModel;
import com.itellyou.service.software.SoftwareReleaseService;
import com.itellyou.service.software.SoftwareUpdaterService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_RELEASE_KEY)
@Service
public class SoftwareReleaseServiceImpl implements SoftwareReleaseService {

    private final SoftwareReleaseDao releaseDao;
    private final SoftwareUpdaterService updaterService;

    public SoftwareReleaseServiceImpl(SoftwareReleaseDao releaseDao, SoftwareUpdaterService updaterService) {
        this.releaseDao = releaseDao;
        this.updaterService = updaterService;
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_RELEASE_KEY , allEntries = true)
    public int add(SoftwareReleaseModel model) {
        return releaseDao.add(model);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_RELEASE_KEY , allEntries = true)
    public int addAll(Collection<SoftwareReleaseModel> releaseValues) {
        return releaseDao.addAll(releaseValues);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_RELEASE_KEY , allEntries = true)
    public int clear(Long softwareId) {
        return releaseDao.clear(softwareId);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_RELEASE_KEY , allEntries = true)
    public int remove(Long id) {
        return releaseDao.remove(id);
    }

    @Override
    public List<SoftwareReleaseDetailModel> search(Collection<Long> softwareIds) {
        StringBuilder keySb = new StringBuilder();
        for (Long id : softwareIds){
            keySb.append(id);
        }
        String key = StringUtils.md5(keySb.toString());
        List<SoftwareReleaseModel> cacheData = RedisUtils.get(CacheKeys.SOFTWARE_RELEASE_KEY,key, List.class);
        if(cacheData == null || cacheData.size() == 0)
        {
            cacheData = releaseDao.search(softwareIds);
            RedisUtils.set(CacheKeys.SOFTWARE_RELEASE_KEY,key,cacheData);
        }
        Collection<Long> ids = new HashSet<>();
        for (SoftwareReleaseModel model: cacheData) {
            ids.add(model.getId());
        }
        if(ids.size() == 0) return new ArrayList<>();
        List<SoftwareUpdaterDetailModel> updaterModels = updaterService.search(ids);
        List<SoftwareReleaseDetailModel> detailModels = new LinkedList<>();
        for (SoftwareReleaseModel model: cacheData) {
            SoftwareReleaseDetailModel detailModel = new SoftwareReleaseDetailModel(model);
            for(SoftwareUpdaterDetailModel updaterModel : updaterModels){
                if(updaterModel.getReleaseId().equals(model.getId())){
                    detailModel.getUpdaters().add(updaterModel);
                }
            }
            detailModels.add(detailModel);
        }
        return detailModels;
    }
}
