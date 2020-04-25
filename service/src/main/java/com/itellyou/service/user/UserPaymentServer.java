package com.itellyou.service.user;

import com.itellyou.model.user.UserPaymentDetailModel;
import com.itellyou.model.user.UserPaymentModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.model.user.UserPaymentType;

import java.util.List;
import java.util.Map;

public interface UserPaymentServer {
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

    UserPaymentDetailModel getDetail(String id);

    int updateStatus(String id , UserPaymentStatus status, Long updatedUserId,Long updatedTime,Long updatedIp) throws Exception;
}
