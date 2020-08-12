package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareVoteDao;
import com.itellyou.model.software.SoftwareVoteModel;
import com.itellyou.service.software.SoftwareVoteService;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "software_vote")
@Service
public class SoftwareVoteSearchServiceImpl implements VoteSearchService<SoftwareVoteModel> , SoftwareVoteService {

    private final SoftwareVoteDao voteDao;

    public SoftwareVoteSearchServiceImpl(SoftwareVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#softwareId).concat('-').concat(#userId)",unless = "#result == null")
    public SoftwareVoteModel findByTargetIdAndUserId(Long softwareId, Long userId) {
        List<SoftwareVoteModel> voteModels = voteDao.search(softwareId != null ? new HashSet<Long>(){{ add(softwareId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<SoftwareVoteModel> search(HashSet<Long> softwareIds, Long userId) {
        return RedisUtils.fetchByCache("software_vote_" + userId, SoftwareVoteModel.class,softwareIds,(HashSet<Long> fetchIds) ->
                voteDao.search(fetchIds,userId)
        ,(SoftwareVoteModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
