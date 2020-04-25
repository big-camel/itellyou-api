package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionAnswerVersionDao {
    int insert(QuestionAnswerVersionModel versionModel);

    Integer findVersionById(Long id);

    List<QuestionAnswerVersionModel> search(@Param("id") Long id,
                                      @Param("answerId") Long answerId,
                                      @Param("questionId") Long questionId,
                                      @Param("userId") String userId,
                                      @Param("hasContent") Boolean hasContent,
                                      @Param("isReviewed") Boolean isReview,
                                      @Param("isDisabled") Boolean isDisable,
                                      @Param("isPublished") Boolean isPublish,
                                      @Param("beginTime") Long beginTime,
                                      @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    Integer getCount(@Param("id") Long id,
                     @Param("answerId") Long answerId,
                     @Param("questionId") Long questionId,
                     @Param("userId") String userId,
                     @Param("isReviewed") Boolean isReview,
                     @Param("isDisabled") Boolean isDisable,
                     @Param("isPublished") Boolean isPublish,
                     @Param("beginTime") Long beginTime,
                     @Param("endTime") Long endTime,
                     @Param("ip") Long ip);
}
