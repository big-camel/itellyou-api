package com.itellyou.dao.user;

import com.itellyou.model.user.UserNotificationActorsModel;
import com.itellyou.model.user.UserNotificationMarkModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserNotificationMarkDao {
    int insertOrUpdate(UserNotificationMarkModel model);

    UserNotificationMarkModel findByUserId(Long userId);
}
