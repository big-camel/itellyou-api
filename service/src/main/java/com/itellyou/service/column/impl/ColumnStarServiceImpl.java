package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnStarDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnStarDetailModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.column.ColumnIndexService;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.column.ColumnStarService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class ColumnStarServiceImpl implements ColumnStarService {

    private final ColumnStarDao starDao;
    private final ColumnInfoService infoService;
    private final ColumnSearchService searchService;
    private final ColumnIndexService indexService;
    private final UserOperationalService operationalService;

    public ColumnStarServiceImpl(ColumnStarDao starDao, ColumnInfoService infoService,ColumnSearchService searchService,ColumnIndexService indexService, UserOperationalService operationalService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.indexService = indexService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(ColumnStarModel model) throws Exception {
        ColumnInfoModel infoModel = searchService.findById(model.getColumnId());
        try{
            if(infoModel == null) throw new Exception("错误的专栏ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStars(model.getColumnId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(model.getColumnId());

        operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.FOLLOW, EntityType.COLUMN,model.getColumnId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));

        return 1;
    }

    @Override
    @Transactional
    public int delete(Long columnId, Long userId) throws Exception {
        try{
            int result = starDao.delete(columnId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStars(columnId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(columnId);
        operationalService.deleteByTargetIdAsync(UserOperationalAction.FOLLOW, EntityType.COLUMN,userId,columnId);
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
