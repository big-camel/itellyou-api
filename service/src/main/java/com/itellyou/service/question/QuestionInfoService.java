package com.itellyou.service.question;

import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionUpdateStepModel;
import com.itellyou.model.sys.RewardType;

public interface QuestionInfoService {
    int insert(QuestionInfoModel questionInfoModel);

    int addStep(QuestionUpdateStepModel... models);

    int updateView(Long userId, Long id, Long ip, String os, String browser);

    int updateAnswers(Long id,Integer value);
    int updateAdopt(Boolean isAdopted,Long adoptionId,Long id);

    int updateComments(Long id, Integer value);

    int updateStarCountById(Long id,Integer step);

    int updateDeleted(boolean deleted, Long id,Long userId,Long ip);

    int updateMetas(Long id, String cover);

    int updateInfo(Long id,
                   String title,
                   String description,
                   RewardType rewardType,
                   Double rewardAdd,
                   Double rewardValue,
                   Long time,
                   Long ip,
                   Long userId);
}
