package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareInfoDao;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.SoftwareEvent;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.common.ViewService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.software.SoftwareInfoService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.service.user.UserDraftService;
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

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_KEY)
@Service
public class SoftwareInfoServiceImpl implements SoftwareInfoService {

    private Logger logger = LoggerFactory.getLogger(SoftwareInfoServiceImpl.class);

    private final SoftwareInfoDao softwareInfoDao;
    private final SoftwareSingleService softwareSingleService;
    private final ViewService viewService;
    private final UserDraftService draftService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public SoftwareInfoServiceImpl(SoftwareInfoDao softwareInfoDao, SoftwareSingleService softwareSingleService, ViewService viewService, UserDraftService draftService, OperationalPublisher operationalPublisher){
        this.softwareInfoDao = softwareInfoDao;
        this.softwareSingleService = softwareSingleService;
        this.viewService = viewService;
        this.draftService = draftService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(SoftwareInfoModel softwareInfoModel) {
        return softwareInfoDao.insert(softwareInfoModel);
    }

    @Override
    public int addStep(DataUpdateStepModel... models) {
        for (DataUpdateStepModel model : models) {
            RedisUtils.remove(CacheKeys.SOFTWARE_KEY,model.getId());
        }
        return softwareInfoDao.addStep(models);
    }

    @Override
    public int updateView(Long userId,Long id,Long ip,String os,String browser) {
        try{
            SoftwareInfoModel softwareModel = softwareSingleService.findById(id);
            if(softwareModel == null) throw new Exception("错误的编号");

            long prevTime = viewService.insertOrUpdate(userId,EntityType.SOFTWARE,id,softwareModel.getName(),ip,os,browser);
            if(DateUtils.getTimestamp() - prevTime > 3600){
                softwareModel.setViewCount(softwareModel.getViewCount() + 1);
                operationalPublisher.publish(new SoftwareEvent(this, EntityAction.VIEW,id,softwareModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
            }
            return softwareModel.getViewCount();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateComments(Long id, Integer value) {
        return softwareInfoDao.updateComments(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateMetas(Long id, String customDescription, String logo) {
        return softwareInfoDao.updateMetas(id,customDescription,logo);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateVote(VoteType type, Integer value, Long id) {
        return softwareInfoDao.updateVote(type,value,id);
    }


    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public int updateDeleted(boolean deleted, Long id,Long userId,Long ip) {
        try {
            SoftwareInfoModel softwareInfoModel = softwareSingleService.findById(id);
            if(softwareInfoModel == null) throw new Exception("未找到文章");
            if(!softwareInfoModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = softwareInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");
            if(deleted){
                //删除用户草稿
                draftService.delete(userId, EntityType.SOFTWARE,id);
            }

            operationalPublisher.publish(new SoftwareEvent(this,deleted ? EntityAction.DELETE : EntityAction.REVERT,
                    id,softwareInfoModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateInfo(Long id, String name, String description, Long groupId, Long time,
                          Long ip,
                          Long userId) {
        return softwareInfoDao.updateInfo(id,name,description,groupId,time,ip,userId);
    }
}
