package com.itellyou.service.question;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerCommentSearchService {
    QuestionAnswerCommentModel findById(Long id);

    List<QuestionAnswerCommentDetailModel> search(HashSet<Long> ids, Long answerId, HashSet<Long> parentIds, Long replyId, Long searchUserId, Long userId,
                                                  Boolean isDeleted,
                                                  Integer childCount,Boolean hasReply,
                                                  Integer minComments, Integer maxComments,
                                                  Integer minSupport, Integer maxSupport,
                                                  Integer minOppose, Integer maxOppose,
                                                  Long beginTime, Long endTime,
                                                  Long ip,
                                                  Map<String, String> order,
                                                  Integer offset,
                                                  Integer limit);

    List<QuestionAnswerCommentDetailModel> search(Long answerId, HashSet<Long> parentIds, Long searchUserId,
                                                  Boolean isDeleted,
                                                  Integer childCount,Boolean hasReply,
                                                  Integer minComments, Integer maxComments,
                                                  Integer minSupport, Integer maxSupport,
                                                  Integer minOppose, Integer maxOppose,
                                                  Long beginTime, Long endTime,
                                                  Map<String, String> order,
                                                  Integer offset,
                                                  Integer limit);

    int count(HashSet<Long> ids, Long answerId, HashSet<Long> parentIds, Long replyId, Long userId,
              Boolean isDeleted,
              Integer minComments, Integer maxComments,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime,
              Long ip);

    int count(Long answerId, HashSet<Long> parentIds,
                    Boolean isDeleted,
                    Integer minComments, Integer maxComments,
                    Integer minSupport, Integer maxSupport,
                    Integer minOppose, Integer maxOppose,
                    Long beginTime, Long endTime);

    PageModel<QuestionAnswerCommentDetailModel> page(Long answerId, HashSet<Long> parentIds, Long searchUserId,
                                                           Boolean isDeleted,
                                                           Integer childCount,Boolean hasReply,
                                                           Integer minComments, Integer maxComments,
                                                           Integer minSupport, Integer maxSupport,
                                                           Integer minOppose, Integer maxOppose,
                                                           Long beginTime, Long endTime,
                                                           Map<String, String> order,
                                                           Integer offset,
                                                           Integer limit);

    QuestionAnswerCommentDetailModel getDetail(Long id, Long answerId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                               Boolean isDeleted,Boolean hasReply);
}
