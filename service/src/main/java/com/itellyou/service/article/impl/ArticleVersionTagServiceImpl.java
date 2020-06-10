package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVersionTagDao;
import com.itellyou.model.article.ArticleVersionTagModel;
import com.itellyou.service.article.ArticleVersionTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "article_version_tag")
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
    public int addAll(Long versionId, HashSet<Long> tagIds) {
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
    public Map<Long, List<ArticleVersionTagModel>> searchTags(HashSet<Long> versionIds) {
        List<ArticleVersionTagModel> models = versionTagDao.searchTags(versionIds);
        Map<Long, List<ArticleVersionTagModel>> map = new LinkedHashMap<>();
        for (ArticleVersionTagModel model : models){
            if(!map.containsKey(model.getVersion())){
                map.put(model.getVersion(),new LinkedList<>());
            }
            map.get(model.getVersion()).add(model);
        }
        return map;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public HashSet<Long> searchTagId(Long versionId) {
        return versionTagDao.searchTagId(versionId);
    }
}
