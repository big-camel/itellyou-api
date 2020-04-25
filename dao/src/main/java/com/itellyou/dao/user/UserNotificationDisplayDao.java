package com.itellyou.dao.user;

import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserNotificationDisplayModel;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserNotificationDisplayDao {
    int insertOrUpdate(@Param("models") UserNotificationDisplayModel... models);
    List<UserNotificationDisplayModel> searchByDefault(@Param("userId") Long userId, @Param("action") UserOperationalAction action, @Param("type") EntityType type);
}
