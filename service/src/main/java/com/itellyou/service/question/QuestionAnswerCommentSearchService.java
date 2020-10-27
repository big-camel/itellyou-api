package com.itellyou.service.question;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerCommentSearchService {
    QuestionAnswerCommentModel findById(Long id);

    List<QuestionAnswerCommentDetailModel> search(Collection<Long> ids, Long answerId, Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId,
                                                  Boolean isDeleted,
                                                  Integer childCount,Boolean hasReply,
                                                  Integer minComment, Integer maxComment,
                                                  Integer minSupport, Integer maxSupport,
                                                  Integer minOppose, Integer maxOppose,
                                                  Long beginTime, Long endTime,
                                                  Long ip,
                                                  Map<String, String> order,
                                                  Integer offset,
                                                  Integer limit);

    List<QuestionAnswerCommentDetailModel> search(Long answerId, Collection<Long> parentIds, Long searchUserId,
                                                  Boolean isDeleted,
                                                  Integer childCount,Boolean hasReply,
                                                  Integer minComment, Integer maxComment,
                                                  Integer minSupport, Integer maxSupport,
                                                  Integer minOppose, Integer maxOppose,
                                                  Long beginTime, Long endTime,
                                                  Map<String, String> order,
                                                  Integer offset,
                                                  Integer limit);

    int count(Collection<Long> ids, Long answerId, Collection<Long> parentIds, Long replyId, Long userId,
              Boolean isDeleted,
              Integer minComment, Integer maxComment,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime,
              Long ip);

    int count(Long answerId, Collection<Long> parentIds,
                    Boolean isDeleted,
                    Integer minComment, Integer maxComment,
                    Integer minSupport, Integer maxSupport,
                    Integer minOppose, Integer maxOppose,
                    Long beginTime, Long endTime);

    PageModel<QuestionAnswerCommentDetailModel> page(Long answerId, Collection<Long> parentIds, Long searchUserId,
                                                           Boolean isDeleted,
                                                           Integer childCount,Boolean hasReply,
                                                           Integer minComment, Integer maxComment,
                                                           Integer minSupport, Integer maxSupport,
                                                           Integer minOppose, Integer maxOppose,
                                                           Long beginTime, Long endTime,
                                                           Map<String, String> order,
                                                           Integer offset,
                                                           Integer limit);

    QuestionAnswerCommentDetailModel getDetail(Long id, Long answerId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                               Boolean isDeleted,Boolean hasReply);
}
