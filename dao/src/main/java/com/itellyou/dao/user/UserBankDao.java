package com.itellyou.dao.user;

import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserBankType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface UserBankDao {
    UserBankModel findByUserId(Long userId);

    int update(@Param("amount") Double amount,@Param("type") UserBankType type,@Param("userId") Long userId);

    int insert(UserBankModel bankModel);

    List<UserBankModel> search(@Param("ids") HashSet<Long> ids);
}
