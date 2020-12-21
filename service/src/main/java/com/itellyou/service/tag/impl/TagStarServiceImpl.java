package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagStarDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.TagEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.service.tag.TagStarSingleService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.TAG_STAR_KEY)
@Service
public class TagStarServiceImpl implements StarService<TagStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagStarDao starDao;
    private final TagInfoService infoService;
    private final TagSearchService searchService;
    private final TagStarSingleService starSingleService;
    private final UserSearchService userSearchService;
    private final OperationalPublisher operationalPublisher;
    private final TagSingleService singleService;

    @Autowired
    public TagStarServiceImpl(TagStarDao starDao, TagInfoService infoService, TagSearchService searchService, TagStarSingleService starSingleService, UserSearchService userSearchService, OperationalPublisher operationalPublisher, TagSingleService singleService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.starSingleService = starSingleService;
        this.userSearchService = userSearchService;
        this.operationalPublisher = operationalPublisher;
        this.singleService = singleService;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#model.tagId).concat('-').concat(#model.createdUserId)")
    public int insert(TagStarModel model) throws Exception {
        TagInfoModel infoModel = singleService.findById(model.getTagId());
        try{
            if(infoModel == null) throw new Exception("错误的标签ID");
            TagStarModel starModel = starSingleService.find(model.getTagId(),model.getCreatedUserId());
            if(starModel != null) return 1;
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCountById(model.getTagId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new TagEvent(this, EntityAction.FOLLOW,model.getTagId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.toLocalDateTime(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        RedisUtils.clear(CacheKeys.TAG_STAR_KEY + "_" + model.getCreatedUserId());
        return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#tagId).concat('-').concat(#userId)")
    public int delete(Long tagId, Long userId,Long ip) throws Exception {
        TagInfoModel infoModel = singleService.findById(tagId);
        try{
            if(infoModel == null) throw new Exception("错误的标签ID");
            TagStarModel starModel = starSingleService.find(tagId,userId);
            if(starModel == null) return 1;

            int result = starDao.delete(tagId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCountById(tagId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new TagEvent(this, EntityAction.UNFOLLOW,tagId,infoModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        RedisUtils.clear(CacheKeys.TAG_STAR_KEY + "_" + userId);
        return 1;
    }

    @Override
    public List<TagStarDetailModel> search(Collection<Long> tagIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<TagStarModel> starModels = starDao.search(tagIds,userId,beginTime,endTime,ip,order,offset,limit);

        List<TagStarDetailModel> detailModels = new ArrayList<>();
        if(starModels.size() == 0) return detailModels;
        Collection<Long> tagFetchIds = new LinkedHashSet<>();
        Collection<Long> userIds = new LinkedHashSet<>();

        for (TagStarModel starModel : starModels){
            TagStarDetailModel detailModel = new TagStarDetailModel(starModel);
            tagFetchIds.add(starModel.getTagId());
            userIds.add(starModel.getCreatedUserId());
            detailModels.add(detailModel);
        }

        // 一次取出所有标签
        List<TagDetailModel> tagDetailModels = searchService.search(tagFetchIds,null,null,null,null,userId,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        List<UserDetailModel> userDetailModels = userSearchService.search(userIds,userId,null,null,null,null,null,null,null,null,null,null);
        for (TagStarDetailModel detailModel : detailModels){
            for (TagDetailModel tagDetailModel :  tagDetailModels){
                if(tagDetailModel.getId().equals(detailModel.getTagId())){
                    detailModel.setTag(tagDetailModel);
                    break;
                }
            }
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getCreatedUserId())){
                    detailModel.setUser(userDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Collection<Long> tagIds, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(tagIds,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<TagStarDetailModel> page(Collection<Long> tagIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<TagStarDetailModel> data = search(tagIds,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(tagIds,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
