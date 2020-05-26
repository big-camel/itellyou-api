package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagGroupDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagGroupModel;
import com.itellyou.service.tag.TagGroupService;
import com.itellyou.service.tag.TagInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

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
    public TagGroupModel findById(Long id) {
        return groupDao.findById(id);
    }

    @Override
    public TagGroupModel findByName(String name) {
        return groupDao.findByName(name);
    }

    @Override
    public int updateTagCountById(Long id, Integer step) {
        return groupDao.updateTagCountById(id,step);
    }

    @Override
    public int updateNameById(Long id, String name) {
        return groupDao.updateNameById(id,name);
    }

    @Override
    @Transactional
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

    @Override
    public List<TagGroupModel> search(Long id, Long userId, Long ip,Boolean isDisabled,Boolean isPublished, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset=0;
        if(limit == null) limit = 10;
        return groupDao.search(id,userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime,order,offset,limit);
    }

    @Override
    public PageModel<TagGroupModel> page(Long id, Long userId, Long ip,Boolean isDisabled,Boolean isPublished, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset=0;
        if(limit == null) limit = 10;
        List<TagGroupModel> data = search(id,userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime,order,offset,limit);
        Integer total = count(userId,ip,minTagCount,maxTagCount,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public List<TagGroupModel> search(Boolean isDisabled,Boolean isPublished,Integer minTagCount, Integer maxTagCount, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,null,isDisabled,isPublished,minTagCount,maxTagCount,null,null,order,offset,limit);
    }

    @Override
    public int count(Long userId, Long ip, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime) {
        return groupDao.count(null,userId,ip,minTagCount,maxTagCount,beginTime,endTime);
    }
}
