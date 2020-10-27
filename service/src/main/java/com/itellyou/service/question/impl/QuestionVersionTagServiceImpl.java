package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionVersionTagDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionVersionTagModel;
import com.itellyou.service.question.QuestionVersionTagService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = CacheKeys.QUESTION_VERSION_TAG_KEY)
public class QuestionVersionTagServiceImpl implements QuestionVersionTagService {

    private final QuestionVersionTagDao versionTagDao;

    public QuestionVersionTagServiceImpl(QuestionVersionTagDao versionTagDao) {
        this.versionTagDao = versionTagDao;
    }

    @Override
    @CacheEvict(key = "#model.version")
    public int add(QuestionVersionTagModel model) {
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
    public Map<Long, List<QuestionVersionTagModel>> searchTags(Collection<Long> versionIds) {
        return RedisUtils.fetch(CacheKeys.QUESTION_VERSION_TAG_KEY,versionIds,
                fetchIds -> versionTagDao.searchTags(fetchIds),
                QuestionVersionTagModel::getVersionId);
    }
}
