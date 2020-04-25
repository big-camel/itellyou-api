package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.model.user.UserStarModel;

import java.util.List;
import java.util.Map;

public interface UserStarService {
    int insert(UserStarModel draftModel) throws Exception;
    int delete(Long userId, Long followerId) throws Exception;
    List<UserStarDetailModel> search(Long userId, Long followerId,Long searchId,
                                     Long beginTime, Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);
    int count(Long userId, Long followerId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserStarDetailModel> page(Long userId, Long followerId,Long searchId,
                                         Long beginTime, Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
}
