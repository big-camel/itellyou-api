package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareUpdaterDao;
import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.software.SoftwareFileModel;
import com.itellyou.model.software.SoftwareUpdaterDetailModel;
import com.itellyou.model.software.SoftwareUpdaterModel;
import com.itellyou.service.software.SoftwareFileService;
import com.itellyou.service.software.SoftwareUpdaterService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@CacheConfig(cacheNames = "software_updater")
@Service
public class SoftwareUpdaterServiceImpl implements SoftwareUpdaterService {

    private final SoftwareUpdaterDao updaterDao;
    private final SoftwareFileService fileService;

    public SoftwareUpdaterServiceImpl(SoftwareUpdaterDao updaterDao, SoftwareFileService fileService) {
        this.updaterDao = updaterDao;
        this.fileService = fileService;
    }

    @Override
    @CacheEvict(value = "software_updater" , allEntries = true)
    public int add(SoftwareUpdaterModel model) {
        return updaterDao.add(model);
    }

    @Override
    @CacheEvict(value = "software_updater" , allEntries = true)
    public int addAll(HashSet<SoftwareUpdaterModel> updaterValues) {
        return updaterDao.addAll(updaterValues);
    }

    @Override
    @CacheEvict(value = "software_updater" , allEntries = true)
    public int clear(Long releaseId) {
        return updaterDao.clear(releaseId);
    }

    @Override
    @CacheEvict(value = "software_updater" , allEntries = true)
    public int remove(Long id) {
        return updaterDao.remove(id);
    }

    @Override
    @Cacheable
    public List<SoftwareUpdaterModel> findByReleaseId(Long releaseId) {
        return updaterDao.search(new HashSet<Long>(){{add(releaseId);}});
    }

    @Override
    public List<SoftwareUpdaterDetailModel> search(HashSet<Long> releaseIds) {
        StringBuilder keySb = new StringBuilder();
        for (Long id : releaseIds){
            keySb.append(id);
        }
        String key = StringUtils.md5(keySb.toString());
        List<SoftwareUpdaterModel> cacheData = RedisUtils.getCache("software_updater",key,List.class);
        if(cacheData == null || cacheData.size() == 0)
        {
            cacheData = updaterDao.search(releaseIds);
            RedisUtils.setCache("software_updater",key,cacheData);
        }
        HashSet<Long> ids = new HashSet<>();
        for (SoftwareUpdaterModel model: cacheData) {
            ids.add(model.getId());
        }
        if(ids.size() == 0) return new ArrayList<>();
        List<SoftwareFileModel> fileModels = fileService.search(ids);
        List<SoftwareUpdaterDetailModel> detailModels = new LinkedList<>();
        for (SoftwareUpdaterModel model: cacheData) {
            SoftwareUpdaterDetailModel detailModel = new SoftwareUpdaterDetailModel(model);
            for(SoftwareFileModel fileModel : fileModels){
                if(fileModel.getUpdaterId().equals(model.getId())){
                    detailModel.getFiles().add(fileModel);
                }
            }
            detailModels.add(detailModel);
        }
        return detailModels;
    }
}
