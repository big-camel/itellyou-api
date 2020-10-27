package com.itellyou.service.user.bank;

import com.itellyou.model.user.UserPaymentModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.model.user.UserPaymentType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserPaymentSingleService {

    UserPaymentModel find(String id);

    List<UserPaymentModel> search(Collection<String> ids,
                                  UserPaymentStatus status, UserPaymentType type,
                                  Long userId,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);
}
