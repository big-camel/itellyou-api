package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareCommentDetailModel;
import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareCommentSearchService {
    SoftwareCommentModel findById(Long id);

    List<SoftwareCommentDetailModel> search(Collection<Long> ids, Long softwareId, Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId,
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

    List<SoftwareCommentDetailModel> search(Long softwareId, Collection<Long> parentIds, Long searchUserId,
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

    int count(Collection<Long> ids, Long softwareId, Collection<Long> parentIds, Long replyId, Long userId,
              Boolean isDeleted,
              Integer minComment, Integer maxComment,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime,
              Long ip);

    int count(Long softwareId, Collection<Long> parentIds,
              Boolean isDeleted,
              Integer minComment, Integer maxComment,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Long beginTime, Long endTime);

    PageModel<SoftwareCommentDetailModel> page(Long softwareId, Collection<Long> parentIds, Long searchUserId,
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

    SoftwareCommentDetailModel getDetail(Long id, Long softwareId, Long parentId, Long replyId, Long searchUserId, Long userId,
                                        Boolean isDeleted, Boolean hasReply);

}
