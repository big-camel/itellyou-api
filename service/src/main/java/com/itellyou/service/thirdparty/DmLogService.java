package com.itellyou.service.thirdparty;

import com.itellyou.model.thirdparty.DmLogModel;

import java.util.List;
import java.util.Map;

public interface DmLogService {

    int insert(DmLogModel dmLogModel);

    int updateStatus(Integer status, Long id);

    List<DmLogModel> search(String templateId, String email, Integer status, Long beginTime, Long endTime, String ip, Map<String, String> order, Integer page, Integer size);

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
    List<DmLogModel> searchByTemplateAndIp(String templateId, Integer status, Long beginTime, String ip, Map<String, String> order, Integer page, Integer size);

    List<DmLogModel> searchByTemplateAndIp(String templateId, Integer status, Long beginTime, String ip);

    List<DmLogModel> searchByTemplateAndIp(String templateId, Long beginTime, String ip);

    /**
     * 根据模版和手机号查询日志
     * @param templateId
     * @param beginTime
     * @param email
     * @param order
     * @param page
     * @param size
     * @return
     */
    List<DmLogModel> searchByTemplateAndEmail(String templateId, Integer status, Long beginTime, String email, Map<String, String> order, Integer page, Integer size);

    List<DmLogModel> searchByTemplateAndEmail(String templateId, Integer status, Long beginTime, String email);

    List<DmLogModel> searchByTemplateAndEmail(String templateId, Long beginTime, String email);

    List<DmLogModel> searchByTemplateAndEmail(String templateId, String email);
}
