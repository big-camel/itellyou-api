package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagStarDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.tag.TagIndexService;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagStarService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class TagStarServiceImpl implements TagStarService {

    private final TagStarDao starDao;
    private final TagInfoService infoService;
    private final TagIndexService indexService;
    private final UserOperationalService operationalService;

    @Autowired
    public TagStarServiceImpl(TagStarDao starDao,TagInfoService infoService,TagIndexService indexService,UserOperationalService operationalService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.indexService = indexService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(TagStarModel model) throws Exception {
        try{
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCountById(model.getTagId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(model.getTagId());
        operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.FOLLOW, EntityType.TAG,model.getTagId(),model.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        return 1;
    }

    @Override
    @Transactional
    public int delete(Long tagId, Long userId) throws Exception {
        try{
            int result = starDao.delete(tagId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCountById(tagId,-1);
            if(result != 1) throw new Exception("更新关注数失败");

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(tagId);
        operationalService.deleteByTargetIdAsync(UserOperationalAction.FOLLOW, EntityType.TAG,userId,tagId);
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
