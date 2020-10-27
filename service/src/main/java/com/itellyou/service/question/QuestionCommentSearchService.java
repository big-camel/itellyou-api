package com.itellyou.service.question;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.question.QuestionCommentModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionCommentSearchService {
    QuestionCommentModel findById(Long id);

    List<QuestionCommentDetailModel> search(Collection<Long> ids, Long questionId, Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId,
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

    List<QuestionCommentDetailModel> search(Long questionId, Collection<Long> parentIds, Long searchUserId,
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

    int count(Collection<Long> ids, Long questionId, Collection<Long> parentIds, Long replyId, Long userId,
              Boolean isDeleted,
              Integer minComment, Integer maxComment,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime,
              Long ip);

    int count(Long questionId, Collection<Long> parentIds,
              Boolean isDeleted,
              Integer minComment, Integer maxComment,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime);

    PageModel<QuestionCommentDetailModel> page(Long questionId, Collection<Long> parentIds, Long searchUserId,
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

    QuestionCommentDetailModel getDetail(Long id, Long questionId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                         Boolean isDeleted,Boolean hasReply);
}
