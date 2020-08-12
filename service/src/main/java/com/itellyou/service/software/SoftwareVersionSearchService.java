package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVersionModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SoftwareVersionSearchService {

    Integer findVersionById(Long id);

    List<SoftwareVersionModel> searchBySoftwareId(Long softwareId, Boolean hasContent);

    List<SoftwareVersionModel> searchBySoftwareId(Long softwareId);

    List<SoftwareVersionModel> searchBySoftwareMap(Map<Long, Integer> softwareMap, Boolean hasContent);

    SoftwareVersionModel findById(Long id);

    SoftwareVersionModel findBySoftwareIdAndId(Long id, Long softwareId);

    SoftwareVersionModel find(Long softwareId, Integer version);

    List<SoftwareVersionModel> search(HashSet<Long> ids,
                                     Map<Long, Integer> softwareMap,
                                     Long userId,
                                     Long groupId,
                                     Boolean hasContent,
                                     Boolean isReview,
                                     Boolean isDisable,
                                     Boolean isPublish,
                                     Long beginTime,
                                     Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);

    Integer count(HashSet<Long> ids,
                  Map<Long, Integer> softwareMap,
                  Long userId,
                  Long groupId,
                  Boolean isReview,
                  Boolean isDisable,
                  Boolean isPublish,
                  Long beginTime,
                  Long endTime,
                  Long ip);
}
