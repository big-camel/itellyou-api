package com.itellyou.service.tag;

import com.itellyou.model.tag.TagVersionDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagVersionSearchService {

    List<TagVersionDetailModel> searchByTagId(Long tagId, Boolean hasContent);

    List<TagVersionDetailModel> searchByTagId(Long tagId);

    List<TagVersionDetailModel> search( Collection<Long> ids,
                                       Map<Long,Integer> tagMap,
                                  Long userId,
                                       Boolean hasContent,
                                       Boolean isReview,
                                       Boolean isDisable,
                                       Boolean isPublish,
                                       Long beginTime,
                                       Long endTime,
                                       Long ip,
                                       Map<String,String> order,
                                       Integer offset,
                                       Integer limit);

    TagVersionDetailModel getDetail(Long id);

    TagVersionDetailModel getDetail(Long tagId,Integer version);
}
