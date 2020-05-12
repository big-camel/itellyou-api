package com.itellyou.dao.user;

import com.itellyou.model.user.UserRankModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserRankDao {

    int insert(UserRankModel model);

    int deleteById(Long id);

    int update(UserRankModel model);

    UserRankModel findByName(String name);

    UserRankModel findById(Long id);

    List<UserRankModel> search(@Param("id") Long id,
                              @Param("name") String name,
                              @Param("minScore") Integer minScore,
                               @Param("maxScore") Integer maxScore,
                              @Param("userId") Long userId,
                              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                              @Param("ip") Long ip,
                              @Param("order") Map<String, String> order,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    int count(@Param("id") Long id,
              @Param("name") String name,
              @Param("minScore") Integer minScore,
              @Param("maxScore") Integer maxScore,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
