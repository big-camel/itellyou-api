package com.itellyou.dao.collab;

import com.itellyou.model.collab.CollabConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CollabConfigDao {
    CollabConfigModel findByKey(String key);
}
