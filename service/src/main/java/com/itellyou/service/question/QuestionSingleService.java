package com.itellyou.service.question;

import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.RewardType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionSingleService {
    QuestionInfoModel findById(Long id);

    List<QuestionInfoModel> search(HashSet<Long> ids, String mode, Long userId, Long searchUserId, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                                  RewardType rewardType,
                                  Double minRewardValue,
                                  Double maxRewardValue,
                                  Integer minComments, Integer maxComments,
                                  Integer minAnswers, Integer maxAnswers,
                                  Integer minView, Integer maxView,
                                  Integer minSupport, Integer maxSupport,
                                  Integer minOppose, Integer maxOppose,
                                  Integer minStar, Integer maxStar,
                                  Long beginTime, Long endTime,
                                  Map<String, String> order, Integer offset, Integer limit);
}
