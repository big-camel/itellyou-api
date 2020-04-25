package com.itellyou.dao.collab;

import com.itellyou.model.collab.CollabInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CollabInfoDao {
    int insert(CollabInfoModel collabInfoModel);

    CollabInfoModel findById(Long id);

    CollabInfoModel findByToken(String token);
}
