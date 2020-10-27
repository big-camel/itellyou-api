package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVersionTagModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareVersionTagService {

    int add(SoftwareVersionTagModel model);

    int addAll(Long versionId, Collection<Long> tagIds);

    int clear(Long versionId);

    int remove(Long versionId, Long tagId);

    Map<Long, List<SoftwareVersionTagModel>> searchTags(Collection<Long> versionIds);
}
