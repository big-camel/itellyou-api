package com.itellyou.service.common;

import com.itellyou.model.sys.VoteType;

import java.util.Map;

public interface VoteService<T> {

    int insert(T voteModel);

    int deleteByTargetIdAndUserId(Long targetId,Long userId);

    Map<String,Object> doVote(VoteType type, Long id, Long userId, Long ip);
}
