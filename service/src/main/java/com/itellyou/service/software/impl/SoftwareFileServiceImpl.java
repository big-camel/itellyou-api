package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareFileDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareFileModel;
import com.itellyou.service.software.SoftwareFileService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_FILE_KEY)
@Service
public class SoftwareFileServiceImpl implements SoftwareFileService {

    private final SoftwareFileDao fileDao;

    public SoftwareFileServiceImpl(SoftwareFileDao fileDao) {
        this.fileDao = fileDao;
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_FILE_KEY , allEntries = true)
    public int add(SoftwareFileModel model) {
        return fileDao.add(model);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_FILE_KEY , allEntries = true)
    public int addAll(Collection<SoftwareFileModel> fileValues) {
        return fileDao.addAll(fileValues);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_FILE_KEY , allEntries = true)
    public int clear(Long updaterId) {
        return fileDao.clear(updaterId);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_FILE_KEY , allEntries = true)
    public int remove(Long id) {
        return fileDao.remove(id);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_FILE_KEY , allEntries = true)
    public int updateRecommendById(boolean isRecommend, Long id) {
        return fileDao.updateRecommendById(isRecommend,id);
    }

    @Override
    public List<SoftwareFileModel> search(Collection<Long> updaterIds) {
        StringBuilder keySb = new StringBuilder();
        for (Long id : updaterIds){
            keySb.append(id);
        }
        String key = StringUtils.md5(keySb.toString());
        List<SoftwareFileModel> cacheData = RedisUtils.get(CacheKeys.SOFTWARE_FILE_KEY,key,List.class);
        if(cacheData == null || cacheData.size() == 0)
        {
            cacheData = fileDao.search(updaterIds);
            RedisUtils.set(CacheKeys.SOFTWARE_FILE_KEY,key,cacheData);
        }
        return cacheData;
    }
}
