package com.itellyou.service.question;

import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.RewardType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionSearchService {

    List<QuestionDetailModel> search(HashSet<Long> ids, String mode, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                                     Integer childCount,
                                     RewardType rewardType,
                                     Double minRewardValue,
                                     Double maxRewardValue,
                                     HashSet<Long> tags,
                                     Integer minComments, Integer maxComments,
                                     Integer minAnswers, Integer maxAnswers,
                                     Integer minView, Integer maxView,
                                     Integer minSupport, Integer maxSupport,
                                     Integer minOppose, Integer maxOppose,
                                     Integer minStar, Integer maxStar,
                                     Long beginTime, Long endTime,
                                     Map<String, String> order, Integer offset, Integer limit);
    int count(HashSet<Long> ids, String mode, Long userId, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
              RewardType rewardType,
              Double minRewardValue,
              Double maxRewardValue,
              HashSet<Long> tags,
              Integer minComments, Integer maxComments,
              Integer minAnswers, Integer maxAnswers,
              Integer minView, Integer maxView,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Integer minStar, Integer maxStar,
              Long beginTime, Long endTime);

    List<QuestionDetailModel> search(HashSet<Long> ids, String mode, Long userId, Long searchUserId, Boolean hasContent,Integer childCount,
                                     Long beginTime, Long endTime,
                                     Map<String, String> order, Integer offset, Integer limit);

    List<QuestionDetailModel> search(Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                                     Integer childCount,
                                     RewardType rewardType,
                                     Double minRewardValue,
                                     Double maxRewardValue,
                                     HashSet<Long> tags,
                                     Integer minComments, Integer maxComments,
                                     Integer minAnswers, Integer maxAnswers,
                                     Integer minView, Integer maxView,
                                     Integer minSupport, Integer maxSupport,
                                     Integer minOppose, Integer maxOppose,
                                     Integer minStar, Integer maxStar,
                                     Long beginTime, Long endTime,
                                     Map<String, String> order, Integer offset, Integer limit);

    List<QuestionDetailModel> search(Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                                     Integer childCount,
                                     RewardType rewardType,
                                     Double minRewardValue,
                                     Double maxRewardValue,
                                     HashSet<Long> tags,
                                     Integer minComments, Integer maxComments,
                                     Integer minAnswers, Integer maxAnswers,
                                     Integer minView, Integer maxView,
                                     Integer minSupport, Integer maxSupport,
                                     Integer minOppose, Integer maxOppose,
                                     Integer minStar, Integer maxStar,
                                     Long beginTime, Long endTime, Integer offset, Integer limit);

    int count(Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
              RewardType rewardType,
              Double minRewardValue,
              Double maxRewardValue,
              HashSet<Long> tags,
              Integer minComments, Integer maxComments,
              Integer minAnswers, Integer maxAnswers,
              Integer minView, Integer maxView,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Integer minStar, Integer maxStar,
              Long beginTime, Long endTime);

    List<QuestionDetailModel> search(Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                                     Long beginTime, Long endTime,
                                     Map<String, String> order, Integer offset, Integer limit);

    List<QuestionDetailModel> search(Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                                     Long beginTime, Long endTime, Integer offset, Integer limit);

    int count(Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime);

    PageModel<QuestionDetailModel> page(Long searchUserId, Long userId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                                        Integer childCount,
                                        RewardType rewardType,
                                        Double minRewardValue,
                                        Double maxRewardValue,
                                        HashSet<Long> tags,
                                        Integer minComments, Integer maxComments,
                                        Integer minAnswers, Integer maxAnswers,
                                        Integer minView, Integer maxView,
                                        Integer minSupport, Integer maxSupport,
                                        Integer minOppose, Integer maxOppose,
                                        Integer minStar, Integer maxStar,
                                        Long beginTime, Long endTime,
                                        Map<String, String> order, Integer offset, Integer limit);

    PageModel<QuestionDetailModel> page(Long searchUserId, Long userId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                                        Long beginTime, Long endTime,
                                        Integer offset,
                                        Integer limit);

    QuestionDetailModel getDetail(Long id, String mode, Long userId);

    QuestionDetailModel getDetail(Long id, String mode);

    QuestionDetailModel getDetail(Long id, Long userId, Long searchUserId);

    QuestionDetailModel getDetail(Long id, Long userId);

    QuestionDetailModel getDetail(Long id);
}
