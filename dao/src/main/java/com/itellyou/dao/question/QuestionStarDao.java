package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionStarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionStarDao {
    int insert(QuestionStarModel model);
    int delete(@Param("questionId") Long questionId, @Param("userId") Long userId);
    List<QuestionStarModel> search(@Param("questionIds") HashSet<Long> questionIds,
                                         @Param("userId") Long userId,
                                         @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                         @Param("ip") Long ip,
                                         @Param("order") Map<String, String> order,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    int count(@Param("questionIds") HashSet<Long> questionIds,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
