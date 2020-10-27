package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionCommentVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface QuestionCommentVoteDao {
    int insert(QuestionCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    List<QuestionCommentVoteModel> search(@Param("commentIds") Collection<Long> commentIds, @Param("userId") Long userId);
}
