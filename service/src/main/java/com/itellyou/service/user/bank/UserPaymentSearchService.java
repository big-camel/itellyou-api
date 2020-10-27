package com.itellyou.service.user.bank;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserPaymentDetailModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.model.user.UserPaymentType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserPaymentSearchService {

    List<UserPaymentDetailModel> search(Collection<String> ids,
                                        UserPaymentStatus status,
                                        UserPaymentType type,
                                        Long userId,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);

    int count(Collection<String> ids,
              UserPaymentStatus status,
              UserPaymentType type,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserPaymentDetailModel> page(Collection<String> ids,
                                           UserPaymentStatus status, UserPaymentType type,
                                           Long userId,
                                           Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);

    UserPaymentDetailModel getDetail(String id);
}
