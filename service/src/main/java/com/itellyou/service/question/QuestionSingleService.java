package com.itellyou.service.question;

import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.RewardType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionSingleService {
    QuestionInfoModel findById(Long id);

    List<QuestionInfoModel> search(Collection<Long> ids, String mode, Long userId, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                                  RewardType rewardType,
                                  Double minRewardValue,
                                  Double maxRewardValue,
                                  Integer minComment, Integer maxComment,
                                  Integer minAnswer, Integer maxAnswer,
                                  Integer minView, Integer maxView,
                                  Integer minSupport, Integer maxSupport,
                                  Integer minOppose, Integer maxOppose,
                                  Integer minStar, Integer maxStar,
                                  Long beginTime, Long endTime,
                                  Map<String, String> order, Integer offset, Integer limit);
}
