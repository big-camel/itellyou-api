package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleCommentVoteDao;
import com.itellyou.model.article.ArticleCommentVoteModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.article.ArticleCommentVoteService;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_COMMENT_VOTE_KEY)
@Service
public class ArticleCommentVoteSearchServiceImpl implements VoteSearchService<ArticleCommentVoteModel> , ArticleCommentVoteService {

    private final ArticleCommentVoteDao voteDao;

    public ArticleCommentVoteSearchServiceImpl(ArticleCommentVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#commentId).concat('-').concat(#userId)",unless = "#result == null")
    public ArticleCommentVoteModel findByTargetIdAndUserId(Long commentId, Long userId) {
        List<ArticleCommentVoteModel> voteModels = voteDao.search(commentId != null ? new HashSet<Long>(){{ add(commentId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<ArticleCommentVoteModel> search(Collection<Long> commentIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.ARTICLE_COMMENT_VOTE_KEY, ArticleCommentVoteModel.class,commentIds,(Collection<Long> fetchIds) ->
                voteDao.search(fetchIds,userId)
        ,id -> id + "-" + userId,ArticleCommentVoteModel::cacheKey);
    }
}
