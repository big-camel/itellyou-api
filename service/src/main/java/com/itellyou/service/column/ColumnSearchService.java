package com.itellyou.service.column;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnInfoModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ColumnSearchService {
    List<ColumnDetailModel> search(HashSet<Long> ids, String name, Long userId,Long memberId, Long searchUserId,
                                   Boolean isDisabled, Boolean isReviewed, Boolean isDeleted,
                                   HashSet<Long> tags,
                                   Integer minArticles, Integer maxArticles,
                                   Integer minStars, Integer maxStars,
                                   Long beginTime, Long endTime,
                                   Long ip,
                                   Map<String, String> order,
                                   Integer offset,
                                   Integer limit);

    int count(HashSet<Long> ids, String name, Long userId,Long memberId,
              Boolean isDisabled, Boolean isReviewed, Boolean isDeleted,
              HashSet<Long> tags,
              Integer minArticles, Integer maxArticles,
              Integer minStars, Integer maxStars,
              Long beginTime, Long endTime, Long ip);

    PageModel<ColumnDetailModel> page(HashSet<Long> ids, String name, Long userId,Long memberId, Long searchUserId,
                                      Boolean isDisabled, Boolean isReviewed, Boolean isDeleted,
                                      HashSet<Long> tags,
                                      Integer minArticles, Integer maxArticles,
                                      Integer minStars, Integer maxStars,
                                      Long beginTime, Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);

    ColumnDetailModel getDetail(Long id);

    ColumnDetailModel getDetail(Long id, Long userId, Long searchUserId);

    ColumnInfoModel findById(Long id);

    ColumnInfoModel findByName(String name);
}
