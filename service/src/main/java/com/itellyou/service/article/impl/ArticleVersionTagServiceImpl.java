package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVersionTagDao;
import com.itellyou.model.article.ArticleVersionTagModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.article.ArticleVersionTagService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = CacheKeys.ARTICLE_VERSION_TAG_KEY)
public class ArticleVersionTagServiceImpl implements ArticleVersionTagService {

    private final ArticleVersionTagDao versionTagDao;

    public ArticleVersionTagServiceImpl(ArticleVersionTagDao versionTagDao) {
        this.versionTagDao = versionTagDao;
    }

    @Override
    @CacheEvict(key = "#model.version")
    public int add(ArticleVersionTagModel model) {
        return versionTagDao.add(model);
    }

    @Override
    @CacheEvict(key = "#versionId")
    public int addAll(Long versionId, Collection<Long> tagIds) {
        return versionTagDao.addAll(versionId,tagIds);
    }

    @Override
    @CacheEvict
    public int clear(Long versionId) {
        return versionTagDao.clear(versionId);
    }

    @Override
    @CacheEvict(key = "#versionId")
    public int remove(Long versionId, Long tagId) {
        return versionTagDao.remove(versionId,tagId);
    }

    @Override
    public Map<Long, List<ArticleVersionTagModel>> searchTags(Collection<Long> versionIds) {
        return RedisUtils.fetch(CacheKeys.ARTICLE_VERSION_TAG_KEY,versionIds,
                (Collection<Long> fetchIds) -> versionTagDao.searchTags(fetchIds),
                ArticleVersionTagModel::getVersion);
    }
}
