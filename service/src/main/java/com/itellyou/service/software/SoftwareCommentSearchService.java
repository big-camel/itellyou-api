package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareCommentDetailModel;
import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.sys.PageModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SoftwareCommentSearchService {
    SoftwareCommentModel findById(Long id);

    List<SoftwareCommentDetailModel> search(HashSet<Long> ids, Long softwareId, HashSet<Long> parentIds, Long replyId, Long searchUserId, Long userId,
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

    List<SoftwareCommentDetailModel> search(Long softwareId, HashSet<Long> parentIds, Long searchUserId,
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

    int count(HashSet<Long> ids, Long softwareId, HashSet<Long> parentIds, Long replyId, Long userId,
              Boolean isDeleted,
              Integer minComments, Integer maxComments,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime,
              Long ip);

    int count(Long softwareId, HashSet<Long> parentIds,
              Boolean isDeleted,
              Integer minComments, Integer maxComments,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime);

    PageModel<SoftwareCommentDetailModel> page(Long softwareId, HashSet<Long> parentIds, Long searchUserId,
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

    SoftwareCommentDetailModel getDetail(Long id, Long softwareId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                        Boolean isDeleted, Boolean hasReply);

}
