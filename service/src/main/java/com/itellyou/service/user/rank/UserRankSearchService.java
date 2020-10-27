package com.itellyou.service.user.rank;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserRankModel;

import java.util.Map;

public interface UserRankSearchService {

    int count(Long id,
              String name,
              Integer minScore,
              Integer maxScore,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserRankModel> page(Long id,
                                  String name,
                                  Integer minScore,
                                  Integer maxScore,
                                  Long userId,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);
}
