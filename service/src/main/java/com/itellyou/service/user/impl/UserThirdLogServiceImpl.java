package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserThirdLogDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserThirdAccountAction;
import com.itellyou.model.user.UserThirdAccountModel;
import com.itellyou.model.user.UserThirdAccountType;
import com.itellyou.model.user.UserThirdLogModel;
import com.itellyou.service.user.UserThirdLogService;
import com.itellyou.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserThirdLogServiceImpl implements UserThirdLogService {

    private final UserThirdLogDao logDao;

    public UserThirdLogServiceImpl(UserThirdLogDao logDao) {
        this.logDao = logDao;
    }

    @Override
    public int insert(UserThirdLogModel model) throws Exception {
        if(StringUtils.isEmpty(model.getId())){
            model.setId(StringUtils.createUUID());
        }
        return logDao.insert(model);
    }

    @Override
    public int delete(String id) {
        return logDao.delete(id);
    }

    @Override
    public int updateVerify(boolean isVerify, String id) {
        return logDao.updateVerify(isVerify,id);
    }

    @Override
    public UserThirdLogModel find(String id) {
        return logDao.find(id);
    }

    @Override
    public List<UserThirdLogModel> search(Long userId, UserThirdAccountType type, UserThirdAccountAction action, Boolean isVerify, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return logDao.search(userId,type,action,isVerify,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long userId, UserThirdAccountType type, UserThirdAccountAction action, Boolean isVerify, Long beginTime, Long endTime, Long ip) {
        return logDao.count(userId,type,action,isVerify,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserThirdLogModel> page(Long userId, UserThirdAccountType type, UserThirdAccountAction action, Boolean isVerify, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserThirdLogModel> data = search(userId,type,action,isVerify,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(userId,type,action,isVerify,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
