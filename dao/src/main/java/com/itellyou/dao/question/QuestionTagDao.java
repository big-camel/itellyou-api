package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface QuestionTagDao {
    int add(QuestionTagModel model);

    int addAll(@Param("questionId") Long questionId, @Param("tagIds") Collection<Long> tagIds);

    int clear(Long questionId);

    int remove(@Param("questionId") Long questionId, @Param("tagId") Long tagId);

    List<QuestionTagModel> searchTags(@Param("questionIds")Collection<Long> questionIds);

    List<QuestionTagModel> searchQuestions(@Param("tagIds") Collection<Long> tagIds);
}
