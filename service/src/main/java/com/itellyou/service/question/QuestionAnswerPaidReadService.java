package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerPaidReadModel;
import com.itellyou.model.user.UserBankLogModel;

public interface QuestionAnswerPaidReadService {

    int insert(QuestionAnswerPaidReadModel model);

    int insertOrUpdate(QuestionAnswerPaidReadModel model);

    int deleteByAnswerId(Long answerId);

    UserBankLogModel doPaidRead(Long answerId, Long userId, Long ip) throws Exception;
}
