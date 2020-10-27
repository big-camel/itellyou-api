package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig( cacheNames = CacheKeys.TAG_KEY )
public class TagSingleServiceImpl implements TagSingleService {

    private final TagInfoDao tagInfoDao;

    public TagSingleServiceImpl(TagInfoDao tagInfoDao) {
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
    public List<TagInfoModel> search(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Long userId, Boolean isDisabled, Boolean isPublished, Long ip, Integer minStar, Integer maxStar, Integer minQuestion, Integer maxQuestion, Integer minArticle, Integer maxArticle, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.TAG_KEY, TagInfoModel.class,ids,(Collection<Long> fetchIds) ->
                tagInfoDao.search(fetchIds,name,mode,groupIds,userId,
                        isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime,order,offset,limit)
        );
    }

    @Override
    @Cacheable(key = "#id" , unless = "#result == null")
    public TagInfoModel findById(Long id) {
        return tagInfoDao.findById(id);
    }

    @Override
    public TagInfoModel findByName(String name) {
        return tagInfoDao.findByName(name);
    }

    public Collection<Long> findNotExist(Collection<Long> source,Collection<Long> target){
        Collection<Long> list = new LinkedHashSet<>();
        for(Long sourceTag:source){
            boolean exist = false;
            if(target != null){
                for(Long targetTag:target){
                    if(sourceTag.equals(targetTag)){
                        exist = true;
                        break;
                    }
                }
            }
            if(!exist){
                list.add(sourceTag);
            }
        }
        return list;
    }
}
