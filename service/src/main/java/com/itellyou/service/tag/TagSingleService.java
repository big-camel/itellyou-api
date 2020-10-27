package com.itellyou.service.tag;

import com.itellyou.model.tag.TagInfoModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagSingleService {

    TagInfoModel findById(Long id);

    TagInfoModel findByName(String name);

    int exists(List<Long> ids);

    int exists(Long... ids);

    List<TagInfoModel> search(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Long userId, Boolean isDisabled, Boolean isPublished, Long ip,
                                Integer minStar, Integer maxStar,
                                Integer minQuestion, Integer maxQuestion,
                                Integer minArticle, Integer maxArticle,
                                Long beginTime, Long endTime,
                                Map<String,String> order,
                                Integer offset,
                                Integer limit);

    Collection<Long> findNotExist(Collection<Long> source,Collection<Long> target);
}
