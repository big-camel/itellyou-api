package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticlePaidReadDao;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticlePaidReadModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.article.ArticlePaidReadSearchService;
import com.itellyou.service.article.ArticlePaidReadService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.user.UserBankService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "article_paid_read")
@Service
public class ArticlePaidReadServiceImpl implements ArticlePaidReadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticlePaidReadDao articlePaidReadDao;
    private final ArticlePaidReadSearchService paidReadSearchService;
    private final UserBankService userBankService;
    private final ArticleSearchService articleSearchService;
    private final OperationalPublisher operationalPublisher;

    public ArticlePaidReadServiceImpl(ArticlePaidReadDao articlePaidReadDao, ArticlePaidReadSearchService paidReadSearchService, UserBankService userBankService, ArticleSearchService articleSearchService, OperationalPublisher operationalPublisher) {
        this.articlePaidReadDao = articlePaidReadDao;
        this.paidReadSearchService = paidReadSearchService;
        this.userBankService = userBankService;
        this.articleSearchService = articleSearchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @CacheEvict
    public int insert(ArticlePaidReadModel model) {
        return articlePaidReadDao.insert(model);
    }

    @Override
    @Transactional
    @CacheEvict
    public int insertOrUpdate(ArticlePaidReadModel model) {
        try{
            ArticlePaidReadModel readModel = paidReadSearchService.findByArticleId(model.getArticleId());
            if (readModel != null) {
                int result = deleteByArticleId(model.getArticleId());
                if(result != 1) throw new Exception("删除失败");
            }
            int result = insert(model);
            if(result != 1) throw new Exception("设置失败");
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#articleId")
    public int deleteByArticleId(Long articleId) {
        return articlePaidReadDao.deleteByArticleId(articleId);
    }

    @Override
    @Transactional
    public UserBankLogModel doPaidRead(Long articleId, Long userId, Long ip) throws Exception {
        try{
            ArticleDetailModel detailModel = articleSearchService.getDetail(articleId);
            if(detailModel == null || detailModel.isDisabled() || detailModel.isDeleted() || !detailModel.isPublished()) throw  new Exception("文章不可用");

            ArticlePaidReadModel paidReadModel = detailModel.getPaidRead();

            Long targetUserId = detailModel.getCreatedUserId();
            if(targetUserId.equals(userId)) throw new Exception("不能给自己支付");

            Double amount = paidReadModel.getPaidAmount();

            UserBankLogModel bankLogModel = userBankService.update(-Math.abs(amount),paidReadModel.getPaidType(), EntityAction.PAYMENT,EntityType.ARTICLE,articleId.toString(),userId,"购买付费阅读内容",ip);
            if(bankLogModel == null) throw new Exception("扣款失败");
            UserBankLogModel targetBankLogModel = userBankService.update(Math.abs(amount),paidReadModel.getPaidType(),EntityAction.PAYMENT,EntityType.ARTICLE,articleId.toString(),targetUserId,"收到付费阅读内容付款",ip);
            if(targetBankLogModel == null) throw new Exception("收款失败");

            if(paidReadModel.getPaidType().equals(UserBankType.CASH)) {
                OperationalModel operationalModel = new OperationalModel(EntityAction.PAYMENT, EntityType.ARTICLE, articleId, targetUserId, userId, DateUtils.getTimestamp(), ip);
                operationalPublisher.publish(new OperationalEvent(this, operationalModel));
            }
            return bankLogModel;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}
