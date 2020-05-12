package com.itellyou.service.question;

import com.itellyou.model.question.QuestionCommentModel;
import com.itellyou.model.sys.VoteType;

public interface QuestionCommentService {
    QuestionCommentModel insert(Long questionId, Long parentId, Long replyId, String content, String html, Long userId, String ip) throws Exception;

    int updateDeleted(Long id, Boolean isDeleted,Long userId,Long ip);

    int updateComments(Long id, Integer value);

    int updateVote(VoteType type,Integer value,Long id);
}
