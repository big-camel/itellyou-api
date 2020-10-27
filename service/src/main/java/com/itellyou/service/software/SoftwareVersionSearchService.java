package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVersionDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareVersionSearchService {

    List<SoftwareVersionDetailModel> searchBySoftwareId(Long softwareId, Boolean hasContent);

    List<SoftwareVersionDetailModel> searchBySoftwareId(Long softwareId);

    SoftwareVersionDetailModel getDetail(Long id);

    SoftwareVersionDetailModel getDetail(Long softwareId, Integer version);

    List<SoftwareVersionDetailModel> search(Collection<Long> ids,
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
}
