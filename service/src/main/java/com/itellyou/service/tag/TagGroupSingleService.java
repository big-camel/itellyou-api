package com.itellyou.service.tag;

import com.itellyou.model.tag.TagGroupModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagGroupSingleService {

    TagGroupModel findById(Long id);

    TagGroupModel findByName(String name);

    List<TagGroupModel> search(Collection<Long> ids,
                               Long userId,
                               Long ip,
                               Boolean isDisabled, Boolean isPublished,
                               Integer minTagCount, Integer maxTagCount,
                               Long beginTime, Long endTime,
                               Map<String,String> order,
                               Integer offset,
                               Integer limit);
}
