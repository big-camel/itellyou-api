package com.itellyou.dao.user;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserDraftDetailModel;
import com.itellyou.model.user.UserDraftModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserDraftDao {
    int insert(UserDraftModel draftModel);
    int insertOrUpdate(UserDraftModel draftModel);
    int exists(@Param("userId") Long userId, @Param("dataType") EntityType dataType, @Param("dataKey") Long dataKey);
    int delete(@Param("userId")Long userId,@Param("dataType") EntityType dataType,@Param("dataKey") Long dataKey);
    List<UserDraftDetailModel> search(@Param("authorId") Long authorId,@Param("dataType") EntityType dataType,@Param("dataKey") Long dataKey,
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);
    int count(@Param("authorId") Long authorId,@Param("dataType") EntityType dataType,@Param("dataKey") Long dataKey,@Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
