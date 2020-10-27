package com.itellyou.service.user.rank.impl;

import com.itellyou.dao.user.UserRankDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.service.user.rank.UserRankSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.USER_RANK_KEY)
@Service
public class UserRankSingleServiceImpl implements UserRankSingleService {

    private final UserRankDao rankDao;
    private final UserBankService bankService;

    public UserRankSingleServiceImpl(UserRankDao rankDao, UserBankService bankService) {
        this.rankDao = rankDao;
        this.bankService = bankService;
    }

    @Override
    public UserRankModel find(List<UserRankModel> list , int score) {
        for (UserRankModel rankModel : list){
            if(score >= rankModel.getMinScore() && score <= rankModel.getMaxScore()){
                return rankModel;
            }
        }
        return null;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public UserRankModel find(int score) {
        return find(all(),score);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#userId).concat('-user')",unless = "#result == null")
    public UserRankModel find(Long userId) {
        UserBankModel bankModel = bankService.findByUserId(userId);
        if(bankModel == null) return null;
        return find(bankModel.getScore());
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#name).concat('-name')",unless = "#result == null")
    public UserRankModel findByName(String name) {
        return rankDao.findByName(name);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#id).concat('-id')",unless = "#result == null")
    public UserRankModel findById(Long id) {
        return rankDao.findById(id);
    }

    @Override
    public List<UserRankModel> all() {
        List<UserRankModel> list = search(null,null,null,null,null,null,null,null,null,null,null);
        if(list != null && list.size() > 0){
            RedisUtils.set(CacheKeys.USER_RANK_KEY,"all",list);
        }
        return list;
    }

    @Override
    public List<UserRankModel> search(Long id, String name, Integer minScore, Integer maxScore, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return rankDao.search(id,name,minScore,maxScore,userId,beginTime,endTime,ip,order,offset,limit);
    }
}
