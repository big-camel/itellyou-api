package com.itellyou.dao.user;

import com.itellyou.model.user.UserLoginLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserLoginLogDao {
    int insert(UserLoginLogModel userLoginLogModel);

    UserLoginLogModel find(String token);

    int setDisabled(@Param("status") Boolean status, @Param("token") String token);
}
