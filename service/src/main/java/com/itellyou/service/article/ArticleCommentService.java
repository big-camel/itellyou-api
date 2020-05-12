package com.itellyou.service.article;

import com.itellyou.model.article.ArticleCommentModel;
import com.itellyou.model.sys.VoteType;

public interface ArticleCommentService {
    ArticleCommentModel insert(Long articleId, Long parentId, Long replyId, String content, String html, Long userId, Long ip,Boolean sendEvent) throws Exception;

    int updateDeleted(Long id, Boolean isDeleted,Long userId,Long ip);

    int updateComments(Long id, Integer value);

    int updateVote(VoteType type,Integer value,Long id);
}
