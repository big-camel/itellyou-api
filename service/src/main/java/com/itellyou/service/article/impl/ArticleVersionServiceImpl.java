package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.dao.article.ArticleVersionDao;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.article.ArticleVersionService;
import com.itellyou.service.article.ArticleVersionTagService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@CacheConfig(cacheNames = "article_version")
@Service
public class ArticleVersionServiceImpl implements ArticleVersionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleVersionDao versionDao;
    private final ArticleInfoDao infoDao;
    private final ArticleVersionTagService versionTagService;

    @Autowired
    public ArticleVersionServiceImpl(ArticleVersionDao articleVersionDao, ArticleInfoDao infoDao, ArticleVersionTagService versionTagService){
        this.versionDao = articleVersionDao;
        this.infoDao = infoDao;
        this.versionTagService = versionTagService;
    }

    @Override
    @Transactional
    public int insert(ArticleVersionModel articleVersionModel) {
        try{
            int rows = versionDao.insert(articleVersionModel);
            if(rows != 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(articleVersionModel.getId());
            articleVersionModel.setVersion(version);
            List<TagDetailModel> tags = articleVersionModel.getTags();
            if(tags != null && tags.size() > 0){
                HashSet<Long> tagIds = new LinkedHashSet<>();
                for (TagDetailModel tagDetailModel : tags){
                    tagIds.add(tagDetailModel.getId());
                }
                rows = versionTagService.addAll(articleVersionModel.getId(),tagIds);
                if(rows != tags.size()){
                    throw new Exception("写入版本标签失败");
                }
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @Transactional
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"), @CacheEvict(key = "T(String).valueOf(#versionModel.articleId).concat('-').concat(#versionModel.version)")})
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

            List<TagDetailModel> tags = versionModel.getTags();
            if(tags != null){
                versionTagService.clear(versionModel.getId());
                if(tags.size() > 0){
                    HashSet<Long> tagIds = new LinkedHashSet<>();
                    for (TagDetailModel tagDetailModel : tags){
                        tagIds.add(tagDetailModel.getId());
                    }
                    rows = versionTagService.addAll(versionModel.getId(),tagIds);
                    if(rows != tags.size()){
                        throw new Exception("更新版本标签失败");
                    }
                }
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateVersion(Long articleId, Integer version, Long ip, Long user) {
        return updateVersion(articleId,version,false,ip,user);
    }

    @Override
    public int updateVersion(Long articleId, Integer version,Boolean isPublished, Long ip, Long user) {
        return updateVersion(articleId,version,null,isPublished, DateUtils.getTimestamp(),ip,user);
    }

    @Override
    public int updateVersion(Long articleId, Integer version, Integer draft,Boolean isPublished, Long time, Long ip, Long user) {
        return infoDao.updateVersion(articleId,version,draft,isPublished,time,ip,user);
    }

    @Override
    @Transactional
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"), @CacheEvict(key = "T(String).valueOf(#versionModel.articleId).concat('-').concat(#versionModel.version)")})
    public int updateVersion(ArticleVersionModel versionModel) {
        try{
            Long versionId = versionModel.getId();
            int result = versionId == null || versionId == 0 ? insert(versionModel) : update(versionModel);
            if(result != 1)
            {
                throw new Exception("更新版本失败");
            }
            result = updateVersion(versionModel.getArticleId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long articleId, Integer version,Boolean isPublished, Long time, Long ip, Long user) {
        return updateVersion(articleId,null,version,isPublished,time,ip,user);
    }

    @Override
    public int updateDraft(Long articleId, Integer version, Long time, Long ip, Long user) {
        return updateDraft(articleId,version,null,time,ip,user);
    }

    @Override
    @Transactional
    public int updateDraft(ArticleVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateDraft(versionModel.getArticleId(),versionModel.getVersion(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新草稿版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
