package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QuestionAnswerCommentVoteDao {
    int insert(QuestionAnswerCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    QuestionAnswerCommentVoteModel findByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);
}
