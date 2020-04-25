package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleStarDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleStarDetailModel;
import com.itellyou.model.article.ArticleStarModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.article.ArticleIndexService;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.article.ArticleStarService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class ArticleStarServiceImpl implements ArticleStarService {

    private final ArticleStarDao starDao;
    private final ArticleInfoService infoService;
    private final ArticleSearchService searchService;
    private final ArticleIndexService indexService;
    private final UserOperationalService operationalService;
    private final UserInfoService userService;

    public ArticleStarServiceImpl(ArticleStarDao starDao, ArticleInfoService infoService,ArticleSearchService searchService,ArticleIndexService indexService, UserOperationalService operationalService,UserInfoService userService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.indexService = indexService;
        this.operationalService = operationalService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public int insert(ArticleStarModel model) throws Exception {
        ArticleInfoModel infoModel = searchService.findById(model.getArticleId());
        try{
            if(infoModel == null) throw new Exception("错误的文章ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入收藏记录失败");
            result = infoService.updateStars(model.getArticleId(),1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(model.getCreatedUserId(),1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(model.getArticleId());

        operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.FOLLOW, EntityType.ARTICLE,model.getArticleId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));

        return 1;
    }

    @Override
    @Transactional
    public int delete(Long articleId, Long userId) throws Exception {
        try{
            int result = starDao.delete(articleId,userId);
            if(result != 1) throw new Exception("删除收藏记录失败");
            result = infoService.updateStars(articleId,-1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(userId,-1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(articleId);
        operationalService.deleteByTargetIdAsync(UserOperationalAction.FOLLOW, EntityType.ARTICLE,userId,articleId);
        return 1;
    }

    @Override
    public List<ArticleStarDetailModel> search(Long articleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return starDao.search(articleId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long articleId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(articleId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ArticleStarDetailModel> page(Long articleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ArticleStarDetailModel> data = search(articleId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(articleId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
