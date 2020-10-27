package com.itellyou.service.question;

import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.sys.VoteType;

public interface QuestionAnswerService {
    int insert(QuestionAnswerModel answerModel);

    int addStep(DataUpdateStepModel... models);

    int updateView(Long userId, Long id, Long ip, String os, String browser);

    int updateComments(Long id, Integer value);

    int updateDisabled(Boolean isDisabled,Long id);

    int updateDeleted(Boolean isDeleted,Long id);

    int updateAdopted(Boolean isAdopted,Long id);

    int updateStarCountById(Long id,Integer step);

    boolean adopt(Long id,Long userId, String ip) throws Exception;

    boolean delete(Long id,Long questionId,Long userId,Long ip) throws Exception;
    boolean revokeDelete(Long id,Long questionId,Long userId,Long ip) throws Exception;

    int updateVote(VoteType type,Integer value,Long id);

    int updateMetas(Long id, String cover);

    int updateInfo(Long id, String description, Long time,
                   Long ip,
                   Long userId);
}
