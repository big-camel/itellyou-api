package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserInfoDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserStarModel;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.service.user.rank.UserRankService;
import com.itellyou.service.user.star.UserStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "user_info")
@Service
public class UserSearchServiceImpl implements UserSearchService {
    private final UserInfoDao infoDao;

    private final UserRankService rankService;
    private final UserBankService bankService;
    private final SysPathService pathService;
    private final UserStarSingleService starSingleService;

    @Autowired
    public UserSearchServiceImpl(UserInfoDao infoDao, UserRankService rankService, UserBankService bankService, SysPathService pathService, UserStarSingleService starSingleService){
        this.infoDao = infoDao;
        this.rankService = rankService;
        this.bankService = bankService;
        this.pathService = pathService;
        this.starSingleService = starSingleService;
    }

    @Override
    public List<UserDetailModel> search(HashSet<Long> ids,
                                        Long searchUserId,
                                        String loginName, String name,
                                        String mobile, String email,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit){

        List<UserDetailModel> detailModels = new ArrayList<>();

        List<UserInfoModel> infoModels = RedisUtils.fetchByCache("user_info",UserInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                infoDao.search(fetchIds,loginName,name,mobile,email,beginTime,endTime,ip,order,offset,limit));
        if(infoModels.size() == 0) return detailModels;
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        for (UserInfoModel infoModel : infoModels){
            UserDetailModel detailModel = new UserDetailModel(infoModel);
            fetchIds.add(infoModel.getId());

            detailModels.add(detailModel);
        }
        // 一次获取路径信息
        List<SysPathModel> pathModels = fetchIds.size() > 0 ? pathService.search(SysPath.USER,fetchIds) : new ArrayList<>();
        // 一次获取需要的银行信息
        List<UserBankModel> bankModels = fetchIds.size() > 0 ? bankService.search(fetchIds) : new ArrayList<>();
        // 一次查出是否关注
        List<UserStarModel> starModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
        }
        for (UserDetailModel detailModel : detailModels){
            // 设置银行信息
            for (UserBankModel bankModel : bankModels){
                if(bankModel.getUserId().equals(detailModel.getId())){
                    detailModel.setBank(bankModel);
                    detailModel.setRank(rankService.find(bankModel.getScore()));
                    break;
                }
            }
            // 设置路径
            for (SysPathModel pathModel : pathModels){
                if(pathModel.getId().equals(detailModel.getId())){
                    detailModel.setPath(pathModel.getPath());
                    break;
                }
            }
            // 设置关注
            for (UserStarModel starModel : starModels){
                if(starModel.getUserId().equals(detailModel.getId())){
                    detailModel.setUseStar(true);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(HashSet<Long> ids, String loginName, String name, String mobile, String email, Long beginTime, Long endTime, Long ip) {
        return infoDao.count(ids,loginName,name,mobile,email,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserDetailModel> page(HashSet<Long> ids, Long searchUserId, String loginName, String name, String mobile, String email, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserDetailModel> data = search(ids,searchUserId,loginName,name,mobile,email,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,loginName,name,mobile,email,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public UserDetailModel find(Long id, Long searchId) {
        List<UserDetailModel> detailModels = search(id != null ? new LinkedHashSet<Long>(){{add(id);}} : null,searchId,null,null,null,null,null,null,null,null,0,1);
        if(detailModels != null && detailModels.size() > 0) return detailModels.get(0);
        return null;
    }
}
