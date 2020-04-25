package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVoteModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionAnswerVoteService {
    int insert(QuestionAnswerVoteModel voteModel);

    int deleteByAnswerIdAndUserId(Long answerId,Long userId);

    QuestionAnswerVoteModel findByAnswerIdAndUserId(Long answerId, Long userId);
}
