package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnInfoDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.service.column.ColumnSearchService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "column")
@Service
public class ColumnSearchServiceImpl implements ColumnSearchService {

    private final ColumnInfoDao columnInfoDao;

    public ColumnSearchServiceImpl(ColumnInfoDao columnInfoDao){
        this.columnInfoDao = columnInfoDao;
    }

    @Override
    public List<ColumnDetailModel> search(HashSet<Long> ids, String name, Long userId,Long memberId, Long searchUserId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, List<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return columnInfoDao.search(ids,name,userId,memberId,searchUserId,isDisabled,isReviewed,isDeleted,tags,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids,String name, Long userId,Long memberId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, List<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        return columnInfoDao.count(ids,name,userId,memberId,isDisabled,isReviewed,isDeleted,tags,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ColumnDetailModel> page(HashSet<Long> ids, String name, Long userId,Long memberId, Long searchUserId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, List<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<ColumnDetailModel> data = search(ids,name,userId,memberId,searchUserId,isDisabled,isReviewed,isDeleted,tags,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,name,userId,memberId,isDisabled,isReviewed,isDeleted,tags,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public ColumnDetailModel getDetail(Long id) {
        return getDetail(id,null,null);
    }

    @Override
    public ColumnDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<ColumnDetailModel> list = search(new HashSet<Long>(){{add(id);}},null,userId,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    @Cacheable
    public ColumnInfoModel findById(Long id) {
        return columnInfoDao.findById(id);
    }

    @Override
    public ColumnInfoModel findByName(String name) {
        return columnInfoDao.findByName(name);
    }
}
