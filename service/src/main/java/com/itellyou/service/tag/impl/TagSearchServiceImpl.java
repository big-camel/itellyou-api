package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.tag.TagSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class TagSearchServiceImpl implements TagSearchService {

    private final TagInfoDao tagInfoDao;

    @Autowired
    public TagSearchServiceImpl(TagInfoDao tagInfoDao){
        this.tagInfoDao = tagInfoDao;
    }

    @Override
    public int exists(List<Long> ids) {
        Long[] idArray = new Long[ids.size()];
        ids.toArray(idArray);
        return exists(idArray);
    }

    @Override
    public int exists(Long... ids) {
        return tagInfoDao.exists(ids);
    }

    @Override
    public List<TagDetailModel> search(HashSet<Long> ids, String name, String mode, Long groupId, Long userId,
                                       Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip,
                                       Integer minStar, Integer maxStar,
                                       Integer minQuestion, Integer maxQuestion,
                                       Integer minArticle, Integer maxArticle,
                                       Long beginTime, Long endTime,
                                       Map<String,String> order,
                                       Integer offset,
                                       Integer limit) {
        return tagInfoDao.search(ids,name,mode,groupId,userId,searchUserId,hasContent,
                isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, String name, String mode, Long groupId, Long userId, Boolean isDisabled, Boolean isPublished, Long ip, Integer minStar, Integer maxStar, Integer minQuestion, Integer maxQuestion, Integer minArticle, Integer maxArticle, Long beginTime, Long endTime) {
        return tagInfoDao.count(ids,name,mode,groupId,userId,
                isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime);
    }

    @Override
    public List<TagDetailModel> search(String name,String mode, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,name,mode,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<TagDetailModel> search(String name, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(name,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<TagDetailModel> search(String name,String mode, Integer offset, Integer limit) {
        return search(name,mode,null,null,null,offset,limit);
    }

    @Override
    public List<TagDetailModel> search(String name, Integer offset, Integer limit) {
        return search(name,null,offset,limit);
    }

    @Override
    public PageModel<TagDetailModel> page(String name, String mode, Long groupId, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip, Integer minStar, Integer maxStar, Integer minQuestion, Integer maxQuestion, Integer minArticle, Integer maxArticle, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<TagDetailModel> list = search(null,name,mode,groupId,userId,searchUserId,hasContent,isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime,order,offset,limit);
        Integer total = count(null,name,mode,groupId,userId,isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime);
        return new PageModel<>(offset,limit,total,list);
    }

    @Override
    public TagInfoModel findById(Long id) {
        return tagInfoDao.findById(id);
    }

    @Override
    public TagInfoModel findByName(String name) {
        return tagInfoDao.findByName(name);
    }

    @Override
    public TagDetailModel getDetail(Long id, Long userId,String mode, Long searchUserId, Boolean hasContent) {
        List<TagDetailModel> listTag = search(new HashSet<Long>(){{add(id);}},null,mode,null,userId,searchUserId,hasContent,null,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return listTag != null && listTag.size() > 0 ? listTag.get(0) : null;
    }

    @Override
    public TagDetailModel getDetail(Long id,String mode, Long userId) {
        return getDetail(id,userId,mode,null,null);
    }

    @Override
    public TagDetailModel getDetail(Long id) {
        return getDetail(id,null,null);
    }
}
