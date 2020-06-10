package com.itellyou.service.user.star;

import com.itellyou.model.user.UserStarModel;

import java.util.HashSet;
import java.util.List;

public interface UserStarSingleService {

    UserStarModel find(Long userId,Long followerId);

    List<UserStarModel> search(HashSet<Long> userIds, Long followerId);
}
