package com.itellyou.service.tag;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagGroupDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagGroupSearchService {

    List<TagGroupDetailModel> search(Collection<Long> ids,
                               Long userId,
                                     Integer childCount,
                               Long ip,
                               Boolean isDisabled, Boolean isPublished,
                               Integer minTagCount, Integer maxTagCount,
                               Long beginTime, Long endTime,
                               Map<String,String> order,
                               Integer offset,
                               Integer limit);

    PageModel<TagGroupDetailModel> page(Collection<Long> ids,
                                        Long userId,
                                        Integer childCount,
                                        Long ip, Boolean isDisabled, Boolean isPublished,
                                        Integer minTagCount, Integer maxTagCount,
                                        Long beginTime, Long endTime,
                                        Map<String,String> order,
                                        Integer offset,
                                        Integer limit);

    int count(Collection<Long> ids,
              Long userId,
              Long ip,Boolean isDisabled,Boolean isPublished,
              Integer minTagCount, Integer maxTagCount,
              Long beginTime, Long endTime);
}
