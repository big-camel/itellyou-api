package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface QuestionAnswerCommentVoteDao {
    int insert(QuestionAnswerCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    List<QuestionAnswerCommentVoteModel> search(@Param("commentIds") Collection<Long> commentIds, @Param("userId") Long userId);
}
