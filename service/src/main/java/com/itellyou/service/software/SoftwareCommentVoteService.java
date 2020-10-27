package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareCommentVoteModel;

import java.util.Collection;
import java.util.List;

public interface SoftwareCommentVoteService {

    List<SoftwareCommentVoteModel> search(Collection<Long> commentIds, Long userId);
}
