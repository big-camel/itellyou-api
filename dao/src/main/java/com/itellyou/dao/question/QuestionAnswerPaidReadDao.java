package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerPaidReadModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QuestionAnswerPaidReadDao {

    int insert(QuestionAnswerPaidReadModel model);

    int deleteByAnswerId(Long answerId);

    QuestionAnswerPaidReadModel findByAnswerId(Long answerId);
}
