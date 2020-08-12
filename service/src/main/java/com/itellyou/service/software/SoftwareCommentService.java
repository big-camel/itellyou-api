package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.sys.VoteType;

public interface SoftwareCommentService {
    SoftwareCommentModel insert(Long softwareId, Long parentId, Long replyId, String content, String html, Long userId, Long ip, Boolean sendEvent) throws Exception;

    int updateDeleted(Long id, Boolean isDeleted, Long userId, Long ip);

    int updateComments(Long id, Integer value);

    int updateVote(VoteType type, Integer value, Long id);
}
