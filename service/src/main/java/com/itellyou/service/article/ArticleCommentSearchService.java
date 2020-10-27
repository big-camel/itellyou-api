package com.itellyou.service.article;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.article.ArticleCommentModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArticleCommentSearchService {
    ArticleCommentModel findById(Long id);

    List<ArticleCommentDetailModel> search(Collection<Long> ids, Long articleId, Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId,
                                           Boolean isDeleted,
                                           Integer childCount,
                                           Boolean hasReply,
                                           Integer minComment, Integer maxComment,
                                           Integer minSupport, Integer maxSupport,
                                           Integer minOppose, Integer maxOppose,
                                           Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);

    List<ArticleCommentDetailModel> search(Long articleId, Collection<Long> parentIds, Long searchUserId,
                                           Boolean isDeleted,
                                           Integer childCount,
                                           Boolean hasReply,
                                           Integer minComment, Integer maxComment,
                                           Integer minSupport, Integer maxSupport,
                                           Integer minOppose, Integer maxOppose,
                                           Long beginTime, Long endTime,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);

    int count(Collection<Long> ids, Long articleId, Collection<Long> parentIds, Long replyId, Long userId,
                    Boolean isDeleted,
                    Integer minComment, Integer maxComment,
                    Integer minSupport, Integer maxSupport,
                    Integer minOppose, Integer maxOppose,
                    Long beginTime, Long endTime,
                    Long ip);

    int count(Long articleId, Collection<Long> parentIds,
                    Boolean isDeleted,
                    Integer minComment, Integer maxComment,
                    Integer minSupport, Integer maxSupport,
                    Integer minOppose, Integer maxOppose,
                    Long beginTime, Long endTime);

    PageModel<ArticleCommentDetailModel> page(Long articleId, Collection<Long> parentIds, Long searchUserId,
                                                    Boolean isDeleted,
                                                    Integer childCount,
                                                    Boolean hasReply,
                                                    Integer minComment, Integer maxComment,
                                                    Integer minSupport, Integer maxSupport,
                                                    Integer minOppose, Integer maxOppose,
                                                    Long beginTime, Long endTime,
                                                    Map<String, String> order,
                                                    Integer offset,
                                                    Integer limit);

    ArticleCommentDetailModel getDetail(Long id, Long articleId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                        Boolean isDeleted, Boolean hasReply);

}
