package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysLinkDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysLinkModel;
import com.itellyou.service.sys.SysLinkService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "sys_link")
public class SysLinkServiceImpl implements SysLinkService {

    private final SysLinkDao linkDao;

    public SysLinkServiceImpl(SysLinkDao linkDao) {
        this.linkDao = linkDao;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int insert(SysLinkModel mode) {
        return linkDao.insert(mode);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int delete(Long id) {
        return linkDao.delete(id);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<SysLinkModel> search(Long id,
                                     String text,
                                     String link,
                                     String target,
                                     Long userId,
                                     Long beginTime, Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit) {
        return linkDao.search(id,text,link,target,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public int count(Long id, String text, String link, String target, Long userId, Long beginTime, Long endTime, Long ip) {
        return linkDao.count(id,text,link,target,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysLinkModel> page(Long id, String text, String link, String target, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<SysLinkModel> data = search(id,text,link,target,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,text,link,target,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
