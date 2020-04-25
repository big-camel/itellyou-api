package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.article.*;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.*;
import com.itellyou.model.view.ViewInfoModel;
import com.itellyou.service.article.*;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.service.view.ViewInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleInfoServiceImpl implements ArticleInfoService {

    private final ArticleInfoDao articleInfoDao;
    private final ArticleVersionService versionService;
    private final ArticleVoteService voteService;
    private final ArticleIndexService indexService;
    private final ArticleSearchService searchService;
    private final ViewInfoService viewService;
    private final UserOperationalService operationalService;
    private final UserInfoService userInfoService;
    private final UserDraftService draftService;
    private final ColumnInfoService columnInfoService;

    @Autowired
    public ArticleInfoServiceImpl(ArticleInfoDao articleInfoDao, ArticleVersionService versionService, ArticleSearchService searchService, ViewInfoService viewService, ArticleVoteService voteService, ArticleIndexService indexService, UserOperationalService operationalService, UserInfoService userInfoService, UserDraftService draftService, ColumnInfoService columnInfoService){
        this.articleInfoDao = articleInfoDao;
        this.versionService = versionService;
        this.viewService = viewService;
        this.voteService = voteService;
        this.indexService = indexService;
        this.searchService = searchService;
        this.operationalService = operationalService;
        this.userInfoService = userInfoService;
        this.draftService = draftService;
        this.columnInfoService = columnInfoService;
    }

    @Override
    public int insert(ArticleInfoModel articleInfoModel) {
        return articleInfoDao.insert(articleInfoModel);
    }

    @Override
    @Transactional
    public int updateView(Long userId,Long id,Long ip,String os,String browser) {
        try{
            long prevTime = viewService.insertOrUpdate(userId,EntityType.ARTICLE,id,ip,os,browser);

            if(DateUtils.getTimestamp() - prevTime > 3600){
                int result = articleInfoDao.updateView(id,1);
                if(result != 1){
                    throw new Exception("更新浏览次数失败");
                }
                indexService.updateIndex(id);
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateComments(Long id, Integer value) {
        return articleInfoDao.updateComments(id,value);
    }

    @Override
    public int updateStars(Long id, Integer value) {
        return articleInfoDao.updateStars(id,value);
    }

    @Override
    public int updateMetas(Long id, String customDescription, String cover) {
        return articleInfoDao.updateMetas(id,customDescription,cover);
    }

    @Override
    @Transactional
    public Long create(Long userId,Long columnId, ArticleSourceType sourceType,String sourceData, String title, String content, String html, String description, List<TagInfoModel> tags, String remark, String save_type, Long ip) {
        try{
            ArticleInfoModel infoModel = new ArticleInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.getTimestamp());
            infoModel.setCreatedUserId(userId);
            int resultRows = insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入提问失败");
            ArticleVersionModel versionModel = versionService.addVersion(infoModel.getId(),userId,columnId,sourceType,sourceData,title,content,html,description,tags,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            return infoModel.getId();
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }



    @Override
    @Transactional
    public Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip) {
        try{
            Map<String,Object> data = new HashMap<>();
            ArticleVoteModel voteModel = voteService.findByArticleIdAndUserId(id,userId);
            if(voteModel != null){
                int result = voteService.deleteByArticleIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = articleInfoDao.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || voteModel.getType() != type){
                voteModel = new ArticleVoteModel(id,type,DateUtils.getTimestamp(),userId, IPUtils.toLong(ip));
                int result = voteService.insert(voteModel);
                if(result != 1) throw new Exception("写入Vote失败");
                result = articleInfoDao.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            ArticleInfoModel infoModel = searchService.findById(id);
            if(infoModel == null) throw new Exception("获取文章失败");
            if(type.equals(VoteType.SUPPORT)){
                operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.LIKE, EntityType.ARTICLE,infoModel.getId(),infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),IPUtils.toLong(ip)));
            }else{
                operationalService.deleteByTargetIdAsync(UserOperationalAction.LIKE, EntityType.ARTICLE,userId,id);
            }
            indexService.updateIndex(id);
            data.put("id",infoModel.getId());
            data.put("support",infoModel.getSupport());
            data.put("oppose",infoModel.getOppose());
            return data;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    public int updateDeleted(boolean deleted, Long id,Long userId) {
        try {
            ArticleDetailModel articleInfoModel = searchService.getDetail(id,"draft");
            if(articleInfoModel == null) throw new Exception("未找到文章");
            if(!articleInfoModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = articleInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");
            if(deleted){
                indexService.delete(id);
                draftService.delete(userId, EntityType.ARTICLE,id);
            }else{
                indexService.updateIndex(id);
            }
            userInfoService.updateArticleCount(userId,deleted ? -1 : 1);
            ColumnInfoModel columnInfoModel = articleInfoModel.getColumn();
            if(columnInfoModel != null){
                result = columnInfoService.updateArticles(columnInfoModel.getId(),deleted ? -1 : 1);
                if(result != 1) throw new Exception("更新文章数量失败");
            }
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
