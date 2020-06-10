package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVoteDao;
import com.itellyou.model.article.ArticleVoteModel;
import com.itellyou.service.article.ArticleVoteService;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "article_vote")
@Service
public class ArticleVoteSearchServiceImpl implements VoteSearchService<ArticleVoteModel> , ArticleVoteService {

    private final ArticleVoteDao voteDao;

    public ArticleVoteSearchServiceImpl(ArticleVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#articleId).concat('-').concat(#userId)",unless = "#result == null")
    public ArticleVoteModel findByTargetIdAndUserId(Long articleId, Long userId) {
        List<ArticleVoteModel> voteModels = voteDao.search(articleId != null ? new HashSet<Long>(){{ add(articleId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<ArticleVoteModel> search(HashSet<Long> articleIds, Long userId) {
        return RedisUtils.fetchByCache("article_vote_" + userId, ArticleVoteModel.class,articleIds,(HashSet<Long> fetchIds) ->
                voteDao.search(fetchIds,userId)
        ,(ArticleVoteModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
