package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVersionDao;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.article.ArticleVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_VERSION_KEY)
@Service
public class ArticleVersionServiceImpl implements ArticleVersionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleVersionDao versionDao;

    @Autowired
    public ArticleVersionServiceImpl(ArticleVersionDao articleVersionDao){
        this.versionDao = articleVersionDao;
    }

    @Override
    public int insert(ArticleVersionModel articleVersionModel) {
        try{
            int id = versionDao.insert(articleVersionModel);
            if(id < 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(articleVersionModel.getId());
            articleVersionModel.setVersion(version);
            return id;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return 0;
        }
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"),@CacheEvict(key = "T(String).valueOf(#versionModel.id).concat('-nc')"), @CacheEvict(key = "T(String).valueOf(#versionModel.articleId).concat('-').concat(#versionModel.version)")})
    public int update(ArticleVersionModel versionModel) {
        try{
            int rows = versionDao.update(versionModel);
            if(rows != 1){
                throw new Exception("更新版本失败");
            }
            if(versionModel.getVersion() == null || versionModel.getVersion() <= 0 ){
                Integer version = versionDao.findVersionById(versionModel.getId());
                versionModel.setVersion(version);
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
