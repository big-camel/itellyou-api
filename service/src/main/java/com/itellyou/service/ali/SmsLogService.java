package com.itellyou.service.ali;

import com.itellyou.model.ali.SmsLogModel;

import java.util.List;
import java.util.Map;

public interface SmsLogService {

    int insert(SmsLogModel smsLogModel);

    int updateStatus(Integer status,Long id);

    List<SmsLogModel> search(String templateId, String mobile,Integer status, Long beginTime, Long endTime, String ip, Map<String,String > order, Integer page, Integer size);

    /**
     * 根据模版和IP查询日志
     * @param templateId
     * @param beginTime
     * @param ip
     * @param order
     * @param page
     * @param size
     * @return
     */
    List<SmsLogModel> searchByTemplateAndIp(String templateId,Integer status,Long beginTime,String ip, Map<String,String > order,Integer page,Integer size);

    List<SmsLogModel> searchByTemplateAndIp(String templateId,Integer status,Long beginTime,String ip);

    List<SmsLogModel> searchByTemplateAndIp(String templateId,Long beginTime,String ip);

    /**
     * 根据模版和手机号查询日志
     * @param templateId
     * @param beginTime
     * @param mobile
     * @param order
     * @param page
     * @param size
     * @return
     */
    List<SmsLogModel> searchByTemplateAndMobile(String templateId,Integer status,Long beginTime,String mobile, Map<String,String > order,Integer page,Integer size);

    List<SmsLogModel> searchByTemplateAndMobile(String templateId,Integer status,Long beginTime,String mobile);

    List<SmsLogModel> searchByTemplateAndMobile(String templateId,Long beginTime,String mobile);

    List<SmsLogModel> searchByTemplateAndMobile(String templateId,String mobile);
}
