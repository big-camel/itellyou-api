package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVersionModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareVersionSingleService {

    Integer findVersionById(Long id);

    List<SoftwareVersionModel> searchBySoftwareMap(Map<Long, Integer> softwareMap, Boolean hasContent);

    SoftwareVersionModel find(Long id);

    SoftwareVersionModel find(Long softwareId, Integer version);

    List<SoftwareVersionModel> search(Collection<Long> ids,
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

    Integer count(Collection<Long> ids,
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
