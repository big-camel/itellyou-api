package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.DmLogDao;
import com.itellyou.model.ali.DmLogModel;
import com.itellyou.model.ali.DmTemplateModel;
import com.itellyou.service.ali.DmLogService;
import com.itellyou.service.ali.DmTemplateService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DmLogServiceImpl implements DmLogService {

    @Autowired
    private DmLogDao dmLogDao;

    @Autowired
    private DmTemplateService dmTemplateService;

    @Override
    public int insert(DmLogModel dmLogModel) {
        return dmLogDao.insert(dmLogModel);
    }

    @Override
    public int updateStatus(Integer status,Long id){
        return dmLogDao.updateStatus(status,id);
    }

    @Override
    public List<DmLogModel> search(String templateId, String email,Integer status, Long beginTime, Long endTime, String ip, Map<String, String> order, Integer page, Integer size) {
        Long longIp = ip == null ? null : IPUtils.toLong(ip);
        return dmLogDao.search(templateId,email,status,beginTime,endTime,longIp,order,page,size);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndIp(String templateId,Integer status, Long beginTime, String ip, Map<String, String> order, Integer page, Integer size) {
        return search(templateId,null,status,beginTime,null,ip,order,page,size);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndIp(String templateId,Integer status, Long beginTime, String ip) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return searchByTemplateAndIp(templateId,status,beginTime,ip,order,1,100);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndIp(String templateId, Long beginTime, String ip) {
        return searchByTemplateAndIp(templateId,1,beginTime,ip);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndEmail(String templateId,Integer status, Long beginTime, String email, Map<String, String> order, Integer page, Integer size) {
        return search(templateId,email,status,beginTime,null,null,order,page,size);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndEmail(String templateId,Integer status, Long beginTime, String email) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return searchByTemplateAndEmail(templateId,status,beginTime,email,order,1,100);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndEmail(String templateId, Long beginTime, String email) {
        return searchByTemplateAndEmail(templateId,1,beginTime,email);
    }

    @Override
    public List<DmLogModel> searchByTemplateAndEmail(String templateId, String email) {
        DmTemplateModel dmTemplateModel = dmTemplateService.findById(templateId);
        if(dmTemplateModel == null) return null;
        return searchByTemplateAndEmail(dmTemplateModel.getId(),1, DateUtils.getTimestamp() - dmTemplateModel.getExpire(),email);
    }
}
