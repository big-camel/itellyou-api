package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface UserSearchService {

    List<UserDetailModel> search(HashSet<Long> ids,
                                 Long searchUserId,
                                 String loginName, String name,
                                 String mobile, String email,
                                 Long beginTime, Long endTime,
                                 Long ip,
                                 Map<String, String> order,
                                 Integer offset,
                                 Integer limit);

    int count(HashSet<Long> ids,
              String loginName, String name,
              String mobile, String email,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserDetailModel> page(HashSet<Long> ids,
                                    Long searchUserId,
                                    String loginName, String name,
                                    String mobile, String email,
                                    Long beginTime, Long endTime,
                                    Long ip,
                                    Map<String, String> order,
                                    Integer offset,
                                    Integer limit);

    UserDetailModel find(Long id,Long searchId);
}
