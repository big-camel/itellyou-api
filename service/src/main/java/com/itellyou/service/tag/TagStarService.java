package com.itellyou.service.tag;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;

import java.util.List;
import java.util.Map;

public interface TagStarService {
    int insert(TagStarModel model) throws Exception;
    int delete(Long tagId, Long userId) throws Exception;
    List<TagStarDetailModel> search(Long tagId, Long userId,
                                    Long beginTime, Long endTime,
                                    Long ip,
                                    Map<String, String> order,
                                    Integer offset,
                                    Integer limit);
    int count(Long tagId, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<TagStarDetailModel> page(Long tagId, Long userId,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);
}
