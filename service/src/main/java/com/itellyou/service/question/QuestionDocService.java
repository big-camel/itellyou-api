package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.sys.RewardType;

import java.util.Collection;

public interface QuestionDocService {

    Long create(Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, Collection<Long> tagIds, String remark, String save_type, Long ip) throws Exception;

    QuestionVersionModel addVersion(Long id, Long userId, String title, String content, String html, String description,
                                    RewardType rewardType, Double rewardValue, Double rewardAdd, Collection<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

}
