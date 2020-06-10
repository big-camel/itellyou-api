package com.itellyou.service.question;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.question.QuestionCommentModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionCommentSearchService {
    QuestionCommentModel findById(Long id);

    List<QuestionCommentDetailModel> search(HashSet<Long> ids, Long questionId, HashSet<Long> parentIds, Long replyId, Long searchUserId, Long userId,
                                            Boolean isDeleted,
                                            Integer childCount,
                                            Boolean hasReply,
                                            Integer minComments, Integer maxComments,
                                            Integer minSupport, Integer maxSupport,
                                            Integer minOppose, Integer maxOppose,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);

    List<QuestionCommentDetailModel> search(Long questionId, HashSet<Long> parentIds, Long searchUserId,
                                            Boolean isDeleted,
                                            Integer childCount,
                                            Boolean hasReply,
                                            Integer minComments, Integer maxComments,
                                            Integer minSupport, Integer maxSupport,
                                            Integer minOppose, Integer maxOppose,
                                            Long beginTime, Long endTime,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);

    int count(HashSet<Long> ids, Long questionId, HashSet<Long> parentIds, Long replyId, Long userId,
              Boolean isDeleted,
              Integer minComments, Integer maxComments,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime,
              Long ip);

    int count(Long questionId, HashSet<Long> parentIds,
              Boolean isDeleted,
              Integer minComments, Integer maxComments,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime);

    PageModel<QuestionCommentDetailModel> page(Long questionId, HashSet<Long> parentIds, Long searchUserId,
                                               Boolean isDeleted,
                                               Integer childCount,
                                               Boolean hasReply,
                                               Integer minComments, Integer maxComments,
                                               Integer minSupport, Integer maxSupport,
                                               Integer minOppose, Integer maxOppose,
                                               Long beginTime, Long endTime,
                                               Map<String, String> order,
                                               Integer offset,
                                               Integer limit);

    QuestionCommentDetailModel getDetail(Long id, Long questionId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                         Boolean isDeleted,Boolean hasReply);
}
