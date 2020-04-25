package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.SmsLogDao;
import com.itellyou.model.ali.SmsLogModel;
import com.itellyou.model.ali.SmsTemplateModel;
import com.itellyou.service.ali.SmsLogService;
import com.itellyou.service.ali.SmsTemplateService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsLogServiceImpl implements SmsLogService {

    @Autowired
    private SmsLogDao smsLogDao;

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Override
    public int insert(SmsLogModel smsLogModel) {
        return smsLogDao.insert(smsLogModel);
    }

    @Override
    public int updateStatus(Integer status,Long id){
        return smsLogDao.updateStatus(status,id);
    }

    @Override
    public List<SmsLogModel> search(String templateId, String mobile,Integer status, Long beginTime, Long endTime, String ip, Map<String, String> order, Integer page, Integer size) {
        Long longIp = ip == null ? null : IPUtils.toLong(ip);
        return smsLogDao.search(templateId,mobile,status,beginTime,endTime,longIp,order,page,size);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndIp(String templateId,Integer status, Long beginTime, String ip, Map<String, String> order, Integer page, Integer size) {
        return search(templateId,null,status,beginTime,null,ip,order,page,size);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndIp(String templateId,Integer status, Long beginTime, String ip) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return searchByTemplateAndIp(templateId,status,beginTime,ip,order,1,100);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndIp(String templateId, Long beginTime, String ip) {
        return searchByTemplateAndIp(templateId,1,beginTime,ip);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndMobile(String templateId,Integer status, Long beginTime, String mobile, Map<String, String> order, Integer page, Integer size) {
        return search(templateId,mobile,status,beginTime,null,null,order,page,size);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndMobile(String templateId,Integer status, Long beginTime, String mobile) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return searchByTemplateAndMobile(templateId,status,beginTime,mobile,order,1,100);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndMobile(String templateId, Long beginTime, String mobile) {
        return searchByTemplateAndMobile(templateId,1,beginTime,mobile);
    }

    @Override
    public List<SmsLogModel> searchByTemplateAndMobile(String templateId, String mobile) {
        SmsTemplateModel smsTemplateModel = smsTemplateService.findById(templateId);
        if(smsTemplateModel == null) return null;
        return searchByTemplateAndMobile(smsTemplateModel.getId(),1, DateUtils.getTimestamp() - smsTemplateModel.getExpire(),mobile);
    }
}
