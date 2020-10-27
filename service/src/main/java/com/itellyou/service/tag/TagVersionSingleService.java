package com.itellyou.service.tag;

import com.itellyou.model.tag.TagVersionModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagVersionSingleService {

    Integer findVersionById(Long id);

    List<TagVersionModel> searchByTagMap(Map<Long, Integer> tagMap, Boolean hasContent);

    List<TagVersionModel> search(Collection<Long> ids,
                                 Map<Long, Integer> tagMap,
                                 Long userId,
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

    int count(Collection<Long> ids,
              Map<Long, Integer> tagMap,
              Long userId,
              Boolean isReview,
              Boolean isDisable,
              Boolean isPublish,
              Long beginTime,
              Long endTime,
              Long ip);

    TagVersionModel find(Long id);

    TagVersionModel find(Long tagId, Integer version);
}
