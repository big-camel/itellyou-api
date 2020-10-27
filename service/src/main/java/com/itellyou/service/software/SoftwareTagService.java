package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareTagModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareTagService {

    int add(SoftwareTagModel model);

    int addAll(Long softwareId, Collection<Long> tagIds);

    int clear(Long softwareId);

    int remove(Long softwareId, Long tagId);

    Map<Long, List<SoftwareTagModel>> searchTags(Collection<Long> softwareIds);

    Map<Long, List<SoftwareTagModel>> searchSoftwares(Collection<Long> tagIds);

    Collection<Long> searchSoftwareIds(Collection<Long> tagIds);
}
