package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVoteModel;

import java.util.Collection;
import java.util.List;

public interface SoftwareVoteService {

    List<SoftwareVoteModel> search(Collection<Long> softwareIds, Long userId);
}
