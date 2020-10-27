package com.itellyou.service.user.star;

import com.itellyou.model.user.UserStarModel;

import java.util.Collection;
import java.util.List;

public interface UserStarSingleService {

    UserStarModel find(Long userId,Long followerId);

    List<UserStarModel> search(Collection<Long> userIds, Long followerId);
}
