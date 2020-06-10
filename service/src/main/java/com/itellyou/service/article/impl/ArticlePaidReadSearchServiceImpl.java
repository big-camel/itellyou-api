package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticlePaidReadDao;
import com.itellyou.model.article.ArticlePaidReadModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.service.article.ArticlePaidReadSearchService;
import com.itellyou.service.user.bank.UserBankLogService;
import com.itellyou.service.user.star.UserStarSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "article_paid_read")
@Service
public class ArticlePaidReadSearchServiceImpl implements ArticlePaidReadSearchService {

    private final ArticlePaidReadDao articlePaidReadDao;
    private final UserStarSearchService userStarService;
    private final UserBankLogService bankLogService;

    public ArticlePaidReadSearchServiceImpl(ArticlePaidReadDao articlePaidReadDao, UserStarSearchService userStarService, UserBankLogService bankLogService) {
        this.articlePaidReadDao = articlePaidReadDao;
        this.userStarService = userStarService;
        this.bankLogService = bankLogService;
    }

    @Override
    @Cacheable(key = "#articleId",unless = "#result == null")
    public ArticlePaidReadModel findByArticleId(Long articleId) {
        return articlePaidReadDao.findByArticleId(articleId);
    }

    @Override
    public boolean checkRead(ArticlePaidReadModel paidReadModel , Long authorId, Long userId) {
        if(paidReadModel != null && !authorId.equals(userId)){
            if(userId == null) return false;
            if(paidReadModel.getStarToRead()){
                UserStarDetailModel starModel = userStarService.find(authorId,userId);
                if(starModel != null) return true;
            }
            if(paidReadModel.getPaidToRead()){
                List<UserBankLogModel> logModels = bankLogService.search(null,paidReadModel.getPaidType(), EntityAction.PAYMENT, EntityType.ARTICLE,paidReadModel.getArticleId().toString(),userId,null,null,null,null,null,null);
                return logModels != null && logModels.size() > 0 && logModels.get(0).getAmount() < 0;
            }else return false;
        }
        return true;
    }

    @Override
    public List<ArticlePaidReadModel> search(HashSet<Long> articleIds) {
        return RedisUtils.fetchByCache("article_paid_read",ArticlePaidReadModel.class,articleIds,(HashSet<Long> fetchIds) ->
            articlePaidReadDao.search(fetchIds)
        );
    }
}
