package com.itellyou.dao.geetest;

import com.itellyou.model.geetest.GeetestLogModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GeetestLogDao {

    @Insert("insert into geetest_log values(#{key},#{type},#{ip},#{status},#{mode},#{createdUserId},#{createdTime})")
    int insert(GeetestLogModel geetestModel);

    GeetestLogModel findByKey(String key);
}
