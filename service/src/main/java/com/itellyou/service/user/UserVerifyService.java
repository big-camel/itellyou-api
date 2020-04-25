package com.itellyou.service.user;

import com.itellyou.model.user.UserVerifyModel;

import java.util.List;
import java.util.Map;

public interface UserVerifyService {
    int insert(UserVerifyModel model);

    List<UserVerifyModel> search(String key,
                                 String userId,
                                 Boolean isDisabled,
                                 Long beginTime,
                                 Long endTime,
                                 Long ip,
                                 Map<String,String> order,
                                 Integer offset,
                                 Integer limit);
}
