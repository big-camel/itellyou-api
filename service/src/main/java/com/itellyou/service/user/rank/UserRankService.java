package com.itellyou.service.user.rank;

import com.itellyou.model.user.UserRankModel;

public interface UserRankService {

    int insert(UserRankModel model);

    int deleteById(Long id);

    int update(UserRankModel model);
}
