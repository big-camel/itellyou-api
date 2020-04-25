package com.itellyou.dao.user;

import com.itellyou.model.user.UserVerifyModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserVerifyDao {
    int insert(UserVerifyModel model);

    List<UserVerifyModel> search(@Param("key") String key,
                             @Param("userId") String userId,
                             @Param("isDisabled") Boolean isDisabled,
                             @Param("beginTime") Long beginTime,
                             @Param("endTime") Long endTime,
                             @Param("ip") Long ip,
                             @Param("order") Map<String,String> order,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);
}
