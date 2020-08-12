package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareCommentVoteDao;
import com.itellyou.model.software.SoftwareCommentVoteModel;
import com.itellyou.service.software.SoftwareCommentVoteService;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "software_comment_vote")
@Service
public class SoftwareCommentVoteSearchServiceImpl implements VoteSearchService<SoftwareCommentVoteModel> , SoftwareCommentVoteService {

    private final SoftwareCommentVoteDao voteDao;

    public SoftwareCommentVoteSearchServiceImpl(SoftwareCommentVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#commentId).concat('-').concat(#userId)",unless = "#result == null")
    public SoftwareCommentVoteModel findByTargetIdAndUserId(Long commentId, Long userId) {
        List<SoftwareCommentVoteModel> voteModels = voteDao.search(commentId != null ? new HashSet<Long>(){{ add(commentId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<SoftwareCommentVoteModel> search(HashSet<Long> commentIds, Long userId) {
        return RedisUtils.fetchByCache("software_comment_vote_" + userId, SoftwareCommentVoteModel.class,commentIds,(HashSet<Long> fetchIds) ->
                voteDao.search(fetchIds,userId)
        ,(SoftwareCommentVoteModel voteModel,Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
