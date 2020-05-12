package com.itellyou.dao.common;

import com.itellyou.model.common.NotificationMarkModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NotificationMarkDao {
    int insertOrUpdate(NotificationMarkModel model);

    NotificationMarkModel findByUserId(Long userId);
}
