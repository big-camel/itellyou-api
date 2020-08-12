package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareCommentVoteModel;

import java.util.HashSet;
import java.util.List;

public interface SoftwareCommentVoteService {

    List<SoftwareCommentVoteModel> search(HashSet<Long> commentIds, Long userId);
}
