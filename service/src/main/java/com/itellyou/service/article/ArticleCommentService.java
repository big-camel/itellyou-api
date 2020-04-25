package com.itellyou.service.article;

import com.itellyou.model.sys.VoteType;
import com.itellyou.model.article.ArticleCommentModel;

import java.util.Map;

public interface ArticleCommentService {
    ArticleCommentModel insert(Long articleId, Long parentId, Long replyId, String content, String html, Long userId, String ip) throws Exception;

    int updateDeleted(Long id, Boolean isDeleted);

    int updateComments(Long id, Integer value);

    Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip);
}
