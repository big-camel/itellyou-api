package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareInfoModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareSingleService {
    SoftwareInfoModel findById(Long id);
    
    List<SoftwareInfoModel> search(Collection<Long> ids, String mode, Long groupId, Long userId,
                                  Boolean isDisabled, Boolean isPublished, Boolean isDeleted,
                                  Integer minComment, Integer maxComment,
                                  Integer minView, Integer maxView,
                                   Integer minSupport,  Integer maxSupport,
                                   Integer minOppose,  Integer maxOppose,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);
}
