package com.itellyou.service.user;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;

import java.util.List;
import java.util.Map;

public interface UserPaymentService {
    int insert(UserPaymentModel model);

    List<UserPaymentDetailModel> search(String id,
                                        UserPaymentStatus status,
                                        UserPaymentType type,
                                        Long userId,
                                        Long beginTime,Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);

    int count(String id,
              UserPaymentStatus status,
              UserPaymentType type,
              Long userId,
              Long beginTime,Long endTime,
              Long ip);

    PageModel<UserPaymentDetailModel> page(String id,
                                            UserPaymentStatus status, UserPaymentType type,
                                            Long userId,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);

    UserPaymentDetailModel getDetail(String id);

    int updateStatus(String id , UserPaymentStatus status, Long updatedUserId, Long updatedTime, Long updatedIp) throws Exception;

    /**
     * 预创建alipay订单
     * @param subject
     * @param amount
     * @param userId
     * @param ip
     * @return
     * @throws Exception
     */
    AlipayTradePrecreateResponse preCreateAlipay(String subject, double amount, Long userId, Long ip) throws Exception;

    /**
     * 查询alipay订单的支付状态
     * @param id
     * @param userId
     * @param ip
     * @return
     * @throws Exception
     */
    UserPaymentStatus queryAlipay(String id, Long userId, Long ip) throws Exception;
}
