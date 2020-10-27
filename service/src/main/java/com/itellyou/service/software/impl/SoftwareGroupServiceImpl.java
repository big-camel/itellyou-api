package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareGroupDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareGroupModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.software.SoftwareGroupService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_GROUP_KEY)
@Service
public class SoftwareGroupServiceImpl implements SoftwareGroupService {

    private final SoftwareGroupDao groupDao;

    public SoftwareGroupServiceImpl(SoftwareGroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_GROUP_KEY + "_all" , allEntries = true)
    public int add(SoftwareGroupModel model) {
        int result = groupDao.add(model);
        return  result;
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_GROUP_KEY + "_all" , allEntries = true)
    public int addAll(Collection<SoftwareGroupModel> groupValues) {
        int result = groupDao.addAll(groupValues);
        return result;
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_GROUP_KEY + "_all" , allEntries = true)
    public int clear() {
        return groupDao.clear();
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#id") , @CacheEvict(value = CacheKeys.SOFTWARE_GROUP_KEY + "_all" , allEntries = true)})
    public int remove(Long id) {
        return groupDao.remove(id);
    }

    @Override
    @Cacheable(value = CacheKeys.SOFTWARE_GROUP_KEY + "_all")
    public List<SoftwareGroupModel> searchAll() {
        return groupDao.searchAll();
    }

    @Override
    @Cacheable(key = "#id")
    public SoftwareGroupModel searchById(Long id) {
        List<SoftwareGroupModel> list = search(new HashSet<Long>(){{ add(id);}},null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<SoftwareGroupModel> search(Collection<Long> ids, String name, Long userId, Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SOFTWARE_GROUP_KEY, SoftwareGroupModel.class,ids,(Collection<Long> fetchIds) ->
                groupDao.search(fetchIds,name,userId,beginTime,endTime,ip,order,offset,limit)
        );
    }

    @Override
    public int count(Collection<Long> ids, String name, Long userId, Long beginTime, Long endTime, Long ip) {
        return groupDao.count(ids,name,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SoftwareGroupModel> page(Collection<Long> ids, String name, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<SoftwareGroupModel> data = search(ids,name,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,name,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
