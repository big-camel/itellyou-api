package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface QuestionAnswerVoteDao {
    int insert(QuestionAnswerVoteModel voteModel);

    int deleteByAnswerIdAndUserId(@Param("answerId") Long answerId,@Param("userId") Long userId);

    List<QuestionAnswerVoteModel> search(@Param("answerIds") Collection<Long> answerIds, @Param("userId") Long userId);
}
