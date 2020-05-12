package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.ThirdLogDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.thirdparty.ThirdAccountAction;
import com.itellyou.model.thirdparty.ThirdAccountType;
import com.itellyou.model.thirdparty.ThirdLogModel;
import com.itellyou.service.thirdparty.ThirdLogService;
import com.itellyou.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ThirdLogServiceImpl implements ThirdLogService {

    private final ThirdLogDao logDao;

    public ThirdLogServiceImpl(ThirdLogDao logDao) {
        this.logDao = logDao;
    }

    @Override
    public int insert(ThirdLogModel model) throws Exception {
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
    public ThirdLogModel find(String id) {
        return logDao.find(id);
    }

    @Override
    public List<ThirdLogModel> search(Long userId, ThirdAccountType type, ThirdAccountAction action, Boolean isVerify, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return logDao.search(userId,type,action,isVerify,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long userId, ThirdAccountType type, ThirdAccountAction action, Boolean isVerify, Long beginTime, Long endTime, Long ip) {
        return logDao.count(userId,type,action,isVerify,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ThirdLogModel> page(Long userId, ThirdAccountType type, ThirdAccountAction action, Boolean isVerify, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ThirdLogModel> data = search(userId,type,action,isVerify,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(userId,type,action,isVerify,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
