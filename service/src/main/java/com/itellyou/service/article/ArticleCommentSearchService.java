package com.itellyou.service.article;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.article.ArticleCommentModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ArticleCommentSearchService {
    ArticleCommentModel findById(Long id);

    List<ArticleCommentDetailModel> search(HashSet<Long> ids, Long articleId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                           Boolean isDeleted,
                                           Integer childCount,
                                           Integer minComments, Integer maxComments,
                                           Integer minSupport, Integer maxSupport,
                                           Integer minOppose, Integer maxOppose,
                                           Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);

    List<ArticleCommentDetailModel> search(Long articleId, Long parentId, Long searchUserId,
                                           Boolean isDeleted,
                                           Integer childCount,
                                           Integer minComments, Integer maxComments,
                                           Integer minSupport, Integer maxSupport,
                                           Integer minOppose, Integer maxOppose,
                                           Long beginTime, Long endTime,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);

    int count(HashSet<Long> ids, Long articleId, Long parentId, Long replyId, Long userId,
                    Boolean isDeleted,
                    Integer minComments, Integer maxComments,
                    Integer minSupport, Integer maxSupport,
                    Integer minOppose, Integer maxOppose,
                    Long beginTime, Long endTime,
                    Long ip);

    int count(Long articleId, Long parentId,
                    Boolean isDeleted,
                    Integer minComments, Integer maxComments,
                    Integer minSupport, Integer maxSupport,
                    Integer minOppose, Integer maxOppose,
                    Long beginTime, Long endTime);

    PageModel<ArticleCommentDetailModel> page(Long articleId, Long parentId, Long searchUserId,
                                                    Boolean isDeleted,
                                                    Integer childCount,
                                                    Integer minComments, Integer maxComments,
                                                    Integer minSupport, Integer maxSupport,
                                                    Integer minOppose, Integer maxOppose,
                                                    Long beginTime, Long endTime,
                                                    Map<String, String> order,
                                                    Integer offset,
                                                    Integer limit);

    ArticleCommentDetailModel getDetail(Long id, Long articleId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                        Boolean isDeleted);

}
