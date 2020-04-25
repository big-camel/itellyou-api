package com.itellyou.service.tag;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagGroupModel;

import java.util.List;
import java.util.Map;

public interface TagGroupService {
    int insert(TagGroupModel groupModel);

    TagGroupModel findById(Long id);

    int updateTagCountById(Long id,Integer step);

    List<TagGroupModel> search(Long id,
                               Long userId,
                               Long ip,
                               Boolean isDisabled,Boolean isPublished,
                               Integer minTagCount, Integer maxTagCount,
                               Long beginTime, Long endTime,
                               Map<String,String> order,
                               Integer offset,
                               Integer limit);

    PageModel<TagGroupModel> page(Long id,
                                      Long userId,
                                      Long ip,Boolean isDisabled,Boolean isPublished,
                                      Integer minTagCount, Integer maxTagCount,
                                      Long beginTime, Long endTime,
                                      Map<String,String> order,
                                      Integer offset,
                                      Integer limit);

    List<TagGroupModel> search(Boolean isDisabled,Boolean isPublished,Integer minTagCount,Integer maxTagCount,
                               Map<String,String> order,
                               Integer offset,
                               Integer limit);

    int count(Long userId,
              Long ip,
              Boolean isDisabled,Boolean isPublished,
              Integer minTagCount, Integer maxTagCount,
              Long beginTime, Long endTime);
}
