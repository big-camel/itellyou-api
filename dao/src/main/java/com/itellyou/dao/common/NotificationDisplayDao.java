package com.itellyou.dao.common;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.NotificationDisplayModel;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotificationDisplayDao {
    int insertOrUpdate(@Param("models") NotificationDisplayModel... models);
    List<NotificationDisplayModel> searchByDefault(@Param("userId") Long userId, @Param("action") EntityAction action, @Param("type") EntityType type);
    List<NotificationDisplayModel> getDefault();
}
