package com.itellyou.service.question;

import com.itellyou.model.question.QuestionCommentVoteModel;

import java.util.HashSet;
import java.util.List;

public interface QuestionCommentVoteService {

    List<QuestionCommentVoteModel> search(HashSet<Long> commentIds, Long userId);
}
