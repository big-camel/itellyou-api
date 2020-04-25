package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagGroupDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagGroupModel;
import com.itellyou.service.tag.TagGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TagGroupServiceImpl implements TagGroupService {

    private final TagGroupDao groupDao;

    @Autowired
    public TagGroupServiceImpl(TagGroupDao groupDao){
        this.groupDao = groupDao;
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
    public int updateTagCountById(Long id, Integer step) {
        return groupDao.updateTagCountById(id,step);
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
        Integer total = count(userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public List<TagGroupModel> search(Boolean isDisabled,Boolean isPublished,Integer minTagCount, Integer maxTagCount, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,null,isDisabled,isPublished,minTagCount,maxTagCount,null,null,order,offset,limit);
    }

    @Override
    public int count(Long userId, Long ip,Boolean isDisabled,Boolean isPublished, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime) {
        return groupDao.count(null,userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime);
    }
}
