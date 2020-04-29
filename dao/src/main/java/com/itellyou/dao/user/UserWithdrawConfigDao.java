package com.itellyou.dao.user;

import com.itellyou.model.user.UserWithdrawConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserWithdrawConfigDao {

    @Select("select * from user_withdraw_config where id = 'default'")
    UserWithdrawConfigModel getDefault();
}
