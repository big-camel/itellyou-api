package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserRankDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserRankService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "user_rank")
@Service
public class UserRankServiceImpl implements UserRankService {

    private final UserRankDao rankDao;
    private final UserBankService bankService;

    public UserRankServiceImpl(UserRankDao rankDao, UserBankService bankService) {
        this.rankDao = rankDao;
        this.bankService = bankService;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int insert(UserRankModel model) {
        return rankDao.insert(model);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Long id) {
        return rankDao.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int update(UserRankModel model) {
        return rankDao.update(model);
    }

    @Override
    @Cacheable(unless = "#result == null")
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
    @Cacheable(unless = "#result == null")
    public UserRankModel find(Long userId) {
        UserBankModel bankModel = bankService.findByUserId(userId);
        if(bankModel == null) return null;
        return find(bankModel.getScore());
    }

    @Override
    @Cacheable
    public UserRankModel findByName(String name) {
        return rankDao.findByName(name);
    }

    @Override
    @Cacheable
    public UserRankModel findById(Long id) {
        return rankDao.findById(id);
    }

    @Override
    @Cacheable(key = "#methodName")
    public List<UserRankModel> all() {
        return search(null,null,null,null,null,null,null,null,null,null,null);
    }

    @Override
    @Cacheable
    public List<UserRankModel> search(Long id, String name, Integer minScore, Integer maxScore, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return rankDao.search(id,name,minScore,maxScore,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable
    public int count(Long id, String name, Integer minScore, Integer maxScore, Long userId, Long beginTime, Long endTime, Long ip) {
        return rankDao.count(id,name,minScore,maxScore,userId,beginTime,endTime,ip);
    }

    @Override
    @Cacheable
    public PageModel<UserRankModel> page(Long id, String name, Integer minScore, Integer maxScore, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<UserRankModel> data = search(id,name,minScore,maxScore,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,name,minScore,maxScore,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
