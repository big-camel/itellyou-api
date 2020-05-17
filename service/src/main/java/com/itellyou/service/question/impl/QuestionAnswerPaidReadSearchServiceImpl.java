package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerPaidReadDao;
import com.itellyou.model.question.QuestionAnswerPaidReadModel;
import com.itellyou.service.question.QuestionAnswerPaidReadSearchService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "article_paid_read")
@Service
public class QuestionAnswerPaidReadSearchServiceImpl implements QuestionAnswerPaidReadSearchService {
    private final QuestionAnswerPaidReadDao articlePaidReadDao;

    public QuestionAnswerPaidReadSearchServiceImpl(QuestionAnswerPaidReadDao articlePaidReadDao) {
        this.articlePaidReadDao = articlePaidReadDao;
    }

    @Override
    @Cacheable(key = "#answerId")
    public QuestionAnswerPaidReadModel findByAnswerId(Long answerId) {
        return articlePaidReadDao.findByAnswerId(answerId);
    }
}
