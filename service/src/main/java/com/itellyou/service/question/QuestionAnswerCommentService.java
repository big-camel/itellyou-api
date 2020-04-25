package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.sys.VoteType;

import java.util.Map;

public interface QuestionAnswerCommentService {
    QuestionAnswerCommentModel insert(Long answerId, Long parentId, Long replyId, String content , String html, Long userId, String ip) throws Exception;

    int updateDeleted(Long id, Boolean isDeleted);

    int updateComments(Long id, Integer value);

    Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip);
}
