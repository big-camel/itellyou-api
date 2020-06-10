package com.itellyou.service.tag;

import com.itellyou.model.tag.TagVersionModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface TagVersionSearchService {

    Integer findVersionById(Long id);

    List<TagVersionModel> searchByTagId(Long tagId, Boolean hasContent);

    List<TagVersionModel> searchByTagId(Long tagId);

    List<TagVersionModel> searchByTagMap(Map<Long,Integer> tagMap, Boolean hasContent);

    List<TagVersionModel> search( HashSet<Long> ids,
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

    int count ( HashSet<Long> ids,
                    Map<Long,Integer> tagMap,
                    Long userId,
                    Boolean isReview,
                    Boolean isDisable,
                    Boolean isPublish,
                    Long beginTime,
                    Long endTime,
                    Long ip);

    TagVersionModel findById(Long id);

    TagVersionModel findByTagIdAndId(Long id,Long tagId);
}
