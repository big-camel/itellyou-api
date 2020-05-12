package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnInfoDao;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnMemberModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.ColumnEvent;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.column.ColumnMemberService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "column")
@Service
public class ColumnInfoServiceImpl implements ColumnInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ColumnInfoDao columnInfoDao;
    private final ColumnSearchService searchService;
    private final ColumnMemberService memberService;
    private final UserInfoService userService;
    private final SysPathService pathService;
    private final OperationalPublisher operationalPublisher;

    public ColumnInfoServiceImpl(ColumnInfoDao columnInfoDao, ColumnSearchService searchService, ColumnMemberService memberService, UserInfoService userService, SysPathService pathService, OperationalPublisher operationalPublisher){
        this.columnInfoDao = columnInfoDao;
        this.searchService = searchService;
        this.memberService = memberService;
        this.userService = userService;
        this.pathService = pathService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    public int insert(ColumnInfoModel infoModel, Long... tags) throws Exception {
        try{
            int result = columnInfoDao.insert(infoModel);
            if(result != 1) throw new Exception("写入专栏失败");

            result = memberService.insert(new ColumnMemberModel(infoModel.getId(),infoModel.getCreatedUserId(),infoModel.getCreatedTime(),infoModel.getCreatedIp()));
            if(result != 1) throw new Exception("新增专栏成员失败");
            String path = "c_" + infoModel.getId();
            SysPathModel pathModel = pathService.findByPath(path);
            if(pathModel != null){
                path += "_" + DateUtils.getTimestamp();
            }
            result = pathService.insert(new SysPathModel(path, SysPath.COLUMN,infoModel.getId()));
            if(result != 1) throw new Exception("创建专栏路径失败");

            if(tags != null && tags.length > 0){
                result = insertTag(infoModel.getId(),tags);
                if(result != tags.length) throw new Exception("关联标签失败");
            }

            if(infoModel.isReviewed()){
                result = userService.updateColumnCount(infoModel.getCreatedUserId(),1);
                if(result != 1) throw new Exception("更新用户专栏数量失败");

                operationalPublisher.publish(new ColumnEvent(this, EntityAction.PUBLISH,
                        infoModel.getId(),infoModel.getCreatedUserId(),infoModel.getCreatedUserId(), DateUtils.getTimestamp(),infoModel.getCreatedIp()));
            }
            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @CacheEvict(key = "#columnId")
    public int insertTag(Long columnId, Long... tags) {
        return columnInfoDao.insertTag(columnId,tags);
    }

    @Override
    @CacheEvict
    public int deleteTag(Long columnId) {
        return columnInfoDao.deleteTag(columnId);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateArticles(Long id, Integer value) {
        return columnInfoDao.updateArticles(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStars(Long id, Integer value) {
        return columnInfoDao.updateStars(id,value);
    }

    @Override
    @CacheEvict(key = "#model.id")
    public int update(ColumnInfoModel model) {
        return columnInfoDao.update(model);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#model.id")
    public int update(ColumnInfoModel model, String path) throws Exception {
        try{
            path = path.toLowerCase();
            SysPathModel pathModel = pathService.findByTypeAndId(SysPath.COLUMN,model.getId());
            boolean isSame = false;
            if(pathModel != null && pathModel.getPath().equals(path)) {
                isSame = true;
            }
            SysPathModel sysPathModel = new SysPathModel(path,SysPath.COLUMN,model.getId());
            int result = isSame ? 1 : (pathModel == null ? pathService.insert(sysPathModel) : pathService.updateByTypeAndId(sysPathModel));
            if(result != 1) throw new Exception("更新路径失败");
            result = update(model);
            if(result != 1) throw new Exception("更新专栏失败");

            operationalPublisher.publish(new ColumnEvent(this, EntityAction.UPDATE,
                    model.getId(),model.getCreatedUserId(),model.getUpdatedUserId(), DateUtils.getTimestamp(),model.getUpdatedIp()));

            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public int updateDeleted(boolean deleted, Long id,Long userId,Long ip) {
        try {
            ColumnInfoModel columnInfoModel = searchService.findById(id);
            if(columnInfoModel == null) throw new Exception("未找到专栏");
            if(!columnInfoModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = columnInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");

            result = userService.updateColumnCount(userId,deleted ? -1 : 1);
            if(result != 1)throw new Exception("更新用户专栏数量失败");

            operationalPublisher.publish(new ColumnEvent(this,deleted ? EntityAction.DELETE : EntityAction.REVERT,
                    id,columnInfoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));

            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
