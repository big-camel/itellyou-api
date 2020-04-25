package com.itellyou.service.user;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDraftDetailModel;
import com.itellyou.model.user.UserDraftModel;

import java.util.List;
import java.util.Map;

public interface UserDraftService {
    int insert(UserDraftModel draftModel);
    int insertOrUpdate(UserDraftModel draftModel);
    boolean exists(Long userId, EntityType dataType, Long dataKey);
    int delete(Long userId, EntityType dataType,Long dataKey);
    List<UserDraftDetailModel> search(Long authorId,EntityType dataType,Long dataKey,Long userId,
                                      Long beginTime,Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);
    int count(Long authorId,EntityType dataType,Long dataKey,Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserDraftDetailModel> page(Long authorId,EntityType dataType,Long dataKey,Long userId,
                                         Long beginTime,Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
}
