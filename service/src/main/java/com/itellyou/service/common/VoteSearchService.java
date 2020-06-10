package com.itellyou.service.common;

public interface VoteSearchService<T>  {
    T findByTargetIdAndUserId(Long targetId, Long userId);
}
