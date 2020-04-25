package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnInfoDao;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnMemberModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.service.column.ColumnIndexService;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.column.ColumnMemberService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class ColumnInfoServiceImpl implements ColumnInfoService {

    private final ColumnInfoDao columnInfoDao;
    private final ColumnSearchService searchService;
    private final ColumnIndexService indexService;
    private final ColumnMemberService memberService;
    private final UserInfoService userService;
    private final SysPathService pathService;

    public ColumnInfoServiceImpl(ColumnInfoDao columnInfoDao, ColumnSearchService searchService, ColumnIndexService indexService, ColumnMemberService memberService, UserInfoService userService, SysPathService pathService){
        this.columnInfoDao = columnInfoDao;
        this.searchService = searchService;
        this.indexService = indexService;
        this.memberService = memberService;
        this.userService = userService;
        this.pathService = pathService;
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
                indexService.updateIndex(searchService.getDetail(infoModel.getId()));
                result = userService.updateColumnCount(infoModel.getCreatedUserId(),1);
                if(result != 1) throw new Exception("更新用户专栏数量失败");
            }
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int insertTag(Long columnId, Long... tags) {
        return columnInfoDao.insertTag(columnId,tags);
    }

    @Override
    public int deleteTag(Long columnId) {
        return columnInfoDao.deleteTag(columnId);
    }

    @Override
    public int updateArticles(Long id, Integer value) {
        return columnInfoDao.updateArticles(id,value);
    }

    @Override
    public int updateStars(Long id, Integer value) {
        return columnInfoDao.updateStars(id,value);
    }

    @Override
    public int update(ColumnInfoModel model) {
        return columnInfoDao.update(model);
    }

    @Override
    @Transactional
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
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    public int updateDeleted(boolean deleted, Long id,Long userId) {
        try {
            ColumnInfoModel columnInfoModel = searchService.findById(id);
            if(columnInfoModel == null) throw new Exception("未找到专栏");
            if(!columnInfoModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = columnInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");
            if(deleted){
                indexService.delete(id);
            }else{
                indexService.updateIndex(id);
            }
            userService.updateColumnCount(userId,deleted ? -1 : 1);
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
