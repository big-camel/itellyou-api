package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerPaidReadModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface QuestionAnswerPaidReadDao {

    int insert(QuestionAnswerPaidReadModel model);

    int deleteByAnswerId(Long answerId);

    QuestionAnswerPaidReadModel findByAnswerId(Long answerId);

    List<QuestionAnswerPaidReadModel> search(@Param("answerIds") Collection<Long> answerIds);
}
