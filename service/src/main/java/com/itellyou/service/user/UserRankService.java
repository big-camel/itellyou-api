package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserRankModel;

import java.util.List;
import java.util.Map;

public interface UserRankService {

    int insert(UserRankModel model);

    int deleteById(Long id);

    int update(UserRankModel model);

    UserRankModel find(List<UserRankModel> list , int score);

    UserRankModel find(int score);

    UserRankModel find(Long userId);

    UserRankModel findByName(String name);

    UserRankModel findById(Long id);

    List<UserRankModel> all();

    List<UserRankModel> search(Long id,
                               String name,
                               Integer minScore,
                               Integer maxScore,
                               Long userId,
                               Long beginTime, Long endTime,
                               Long ip,
                               Map<String, String> order,
                               Integer offset,
                               Integer limit);

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
