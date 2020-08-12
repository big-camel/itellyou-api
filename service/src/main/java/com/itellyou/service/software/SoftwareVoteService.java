package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVoteModel;

import java.util.HashSet;
import java.util.List;

public interface SoftwareVoteService {

    List<SoftwareVoteModel> search(HashSet<Long> softwareIds, Long userId);
}
