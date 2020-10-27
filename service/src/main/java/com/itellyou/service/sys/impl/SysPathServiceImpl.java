package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysPathDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.SYS_PATH_KEY)
@Service
public class SysPathServiceImpl implements SysPathService {

    private final SysPathDao pathDao;

    @Autowired
    public SysPathServiceImpl(SysPathDao pathDao){
        this.pathDao = pathDao;
    }

    @Override
    @CacheEvict
    public int insert(SysPathModel model) {
        return pathDao.insert(model);
    }

    @Override
    @Cacheable(key = "#path",unless="#result == null")
    public SysPathModel findByPath(String path) {
        return pathDao.findByPath(path);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#id).concat('-').concat(#type.name)" , condition = "#id > 0",unless="#result == null")
    public SysPathModel findByTypeAndId(SysPath type, Long id) {
        return pathDao.findByTypeAndId(type,id);
    }

    @Override
    @CacheEvict
    public int updateByTypeAndId(SysPathModel model) {
        return pathDao.updateByTypeAndId(model);
    }

    @Override
    public List<SysPathModel> search(SysPath type, Collection<Long> ids) {
        return RedisUtils.fetch(CacheKeys.SYS_PATH_KEY,SysPathModel.class,ids,(Collection<Long> fetchIds) -> pathDao.search(type,fetchIds),
                id -> id + "-" + type.getName(),
                SysPathModel::cacheKey);
    }
}
