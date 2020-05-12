package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.sys.VoteType;

public interface QuestionAnswerCommentService {
    QuestionAnswerCommentModel insert(Long answerId, Long parentId, Long replyId, String content , String html, Long userId, Long ip,Boolean sendEvent) throws Exception;

    int updateDeleted(Long id, Boolean isDeleted,Long userId,Long ip);

    int updateComments(Long id, Integer value);

    int updateVote(VoteType type,Integer value,Long id);
}
