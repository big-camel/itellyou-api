package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagGroupDao;
import com.itellyou.model.tag.TagGroupModel;
import com.itellyou.service.tag.TagGroupService;
import com.itellyou.service.tag.TagInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "tag_group")
@Service
public class TagGroupServiceImpl implements TagGroupService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagGroupDao groupDao;
    private final TagInfoService tagService;

    @Autowired
    public TagGroupServiceImpl(TagGroupDao groupDao, TagInfoService tagService){
        this.groupDao = groupDao;
        this.tagService = tagService;
    }
    @Override
    public int insert(TagGroupModel groupModel) {
        return groupDao.insert(groupModel);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateTagCountById(Long id, Integer step) {
        return groupDao.updateTagCountById(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateNameById(Long id, String name) {
        return groupDao.updateNameById(id,name);
    }

    @Override
    @Transactional
    @CacheEvict
    public int deleteById(Long id) {
        try {
            int result = groupDao.deleteById(id);
            if(result != 1) throw new Exception("删除出错了");
            tagService.updateGroupByGroupId(0l,id);
            return 1;
        }
        catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
