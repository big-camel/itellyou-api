package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagStarDao;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.TagEvent;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "tag")
@Service
public class TagStarServiceImpl implements StarService<TagStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagStarDao starDao;
    private final TagInfoService infoService;
    private final TagSearchService searchService;

    private final OperationalPublisher operationalPublisher;

    @Autowired
    public TagStarServiceImpl(TagStarDao starDao, TagInfoService infoService, TagSearchService searchService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#model.tagId")
    public int insert(TagStarModel model) throws Exception {
        TagInfoModel infoModel = searchService.findById(model.getTagId());
        try{
            if(infoModel == null) throw new Exception("错误的标签ID");
            List<TagStarDetailModel> list = search(model.getTagId(),model.getCreatedUserId(),null,null,null,null,null,null);
            if(list!=null && list.size() > 0) return 1;
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCountById(model.getTagId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new TagEvent(this, EntityAction.FOLLOW,model.getTagId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#tagId")
    public int delete(Long tagId, Long userId,Long ip) throws Exception {
        TagInfoModel infoModel = searchService.findById(tagId);
        try{
            if(infoModel == null) throw new Exception("错误的标签ID");
            List<TagStarDetailModel> list = search(tagId,userId,null,null,null,null,null,null);
            if(list == null || list.size() == 0) return 1;

            int result = starDao.delete(tagId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCountById(tagId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new TagEvent(this, EntityAction.UNFOLLOW,tagId,infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    public List<TagStarDetailModel> search(Long tagId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return starDao.search(tagId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long tagId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(tagId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<TagStarDetailModel> page(Long tagId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<TagStarDetailModel> data = search(tagId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(tagId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
