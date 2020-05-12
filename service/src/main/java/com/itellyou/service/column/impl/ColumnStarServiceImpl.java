package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnStarDao;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnStarDetailModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.ColumnEvent;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class ColumnStarServiceImpl implements StarService<ColumnStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ColumnStarDao starDao;
    private final ColumnInfoService infoService;
    private final ColumnSearchService searchService;
    private final OperationalPublisher operationalPublisher;

    public ColumnStarServiceImpl(ColumnStarDao starDao, ColumnInfoService infoService, ColumnSearchService searchService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#model.columnId")
    public int insert(ColumnStarModel model) throws Exception {
        ColumnInfoModel infoModel = searchService.findById(model.getColumnId());
        try{
            if(infoModel == null) throw new Exception("错误的专栏ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStars(model.getColumnId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new ColumnEvent(this, EntityAction.FOLLOW,model.getColumnId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#columnId")
    public int delete(Long columnId, Long userId,Long ip) throws Exception {
        ColumnInfoModel infoModel = searchService.findById(columnId);
        try{
            if(infoModel == null) throw new Exception("错误的专栏ID");
            int result = starDao.delete(columnId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStars(columnId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new ColumnEvent(this, EntityAction.UNFOLLOW,columnId,infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    public List<ColumnStarDetailModel> search(Long columnId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return starDao.search(columnId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long columnId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(columnId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ColumnStarDetailModel> page(Long columnId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ColumnStarDetailModel> data = search(columnId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(columnId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
