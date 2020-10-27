package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerStarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionAnswerStarDao {
    int insert(QuestionAnswerStarModel model);
    int delete(@Param("answerId") Long answerId, @Param("userId") Long userId);
    List<QuestionAnswerStarModel> search(@Param("answerIds") Collection<Long> answerIds,
                                         @Param("userId") Long userId,
                                         @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                         @Param("ip") Long ip,
                                         @Param("order") Map<String, String> order,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    int count(@Param("answerIds") Collection<Long> answerIds,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
