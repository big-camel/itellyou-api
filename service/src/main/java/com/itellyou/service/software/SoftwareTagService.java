package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareTagModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SoftwareTagService {

    int add(SoftwareTagModel model);

    int addAll(Long softwareId, HashSet<Long> tagIds);

    int clear(Long softwareId);

    int remove(Long softwareId, Long tagId);

    Map<Long, List<SoftwareTagModel>> searchTags(HashSet<Long> softwareIds);

    HashSet<Long> searchTagId(Long softwareId);

    HashSet<Long> searchSoftwareId(Long tagId);

    HashSet<Long> searchSoftwareId(HashSet<Long> tagId);
}
