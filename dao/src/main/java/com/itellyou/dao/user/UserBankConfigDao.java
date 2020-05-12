package com.itellyou.dao.user;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankConfigModel;
import com.itellyou.model.user.UserBankType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserBankConfigDao {

    int insert(UserBankConfigModel model);

    int update(UserBankConfigModel model);

    UserBankConfigModel find(@Param("bankType") UserBankType bankType,@Param("action") EntityAction action, @Param("type") EntityType type);

    List<UserBankConfigModel> findByType(UserBankType bankType);
}
