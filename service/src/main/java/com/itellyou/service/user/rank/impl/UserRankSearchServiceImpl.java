package com.itellyou.service.user.rank.impl;

import com.itellyou.dao.user.UserRankDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.user.rank.UserRankSearchService;
import com.itellyou.service.user.rank.UserRankSingleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserRankSearchServiceImpl implements UserRankSearchService {

    private final UserRankDao rankDao;
    private final UserRankSingleService singleService;

    public UserRankSearchServiceImpl(UserRankDao rankDao, UserRankSingleService singleService) {
        this.rankDao = rankDao;
        this.singleService = singleService;
    }

    @Override
    public int count(Long id, String name, Integer minScore, Integer maxScore, Long userId, Long beginTime, Long endTime, Long ip) {
        return rankDao.count(id,name,minScore,maxScore,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserRankModel> page(Long id, String name, Integer minScore, Integer maxScore, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<UserRankModel> data = singleService.search(id,name,minScore,maxScore,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,name,minScore,maxScore,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
