package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;

import java.util.List;
import java.util.Map;

public interface UserWithdrawService {
    int insert(UserWithdrawModel model);

    List<UserWithdrawDetailModel> search(String id,
                                        UserPaymentStatus status,
                                        Long userId,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);

    int count(String id,
              UserPaymentStatus status,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserWithdrawDetailModel> page(String id,
                                            UserPaymentStatus status,
                                            Long userId,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);

    UserWithdrawDetailModel getDetail(String id);

    /**
     * 获取锁定（未处理）的金额
     * @param userId
     * @return
     */
    double getLockedCash(Long userId);

    int updateStatus(String id, UserPaymentStatus status, Long updatedUserId, Long updatedTime, Long updatedIp) throws Exception;

    UserBankLogModel doWithdraw(Long userId,double amount,Long ip) throws Exception;

    int doWithdraw(String withdrawId, Long updatedUserId, Long updatedIp) throws Exception;
}
