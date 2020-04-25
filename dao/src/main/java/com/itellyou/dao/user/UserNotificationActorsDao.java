package com.itellyou.dao.user;

import com.itellyou.model.user.UserNotificationActorsModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserNotificationActorsDao {
    int insert(@Param("models")UserNotificationActorsModel ...models);
}
