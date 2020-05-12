package com.itellyou.dao.common;

import com.itellyou.model.common.NotificationActorsModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NotificationActorsDao {
    int insert(@Param("models") NotificationActorsModel...models);
}
