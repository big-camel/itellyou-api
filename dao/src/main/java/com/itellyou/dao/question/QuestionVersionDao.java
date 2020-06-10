package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionVersionDao {
    int insert(QuestionVersionModel versionModel);

    int update(QuestionVersionModel versionModel);

    Integer findVersionById(Long id);

    List<QuestionVersionModel> search(@Param("ids") HashSet<Long> ids,
                                      @Param("questionMap") Map<Long,Integer> questionMap,
                             @Param("userId") Long userId,
                             @Param("hasContent") Boolean hasContent,
                             @Param("isReviewed") Boolean isReview,
                             @Param("isDisabled") Boolean isDisable,
                             @Param("isPublished") Boolean isPublish,
                             @Param("beginTime") Long beginTime,
                             @Param("endTime") Long endTime,
                             @Param("ip") Long ip,
                             @Param("order") Map<String,String> order,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    Integer count(@Param("ids") HashSet<Long> ids,
                     @Param("questionMap") Map<Long,Integer> questionMap,
                     @Param("userId") Long userId,
                     @Param("isReviewed") Boolean isReview,
                     @Param("isDisabled") Boolean isDisable,
                     @Param("isPublished") Boolean isPublish,
                     @Param("beginTime") Long beginTime,
                     @Param("endTime") Long endTime,
                     @Param("ip") Long ip);
}
