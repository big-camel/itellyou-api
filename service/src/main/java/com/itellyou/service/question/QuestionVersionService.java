package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.tag.TagInfoModel;

import java.util.List;

public interface QuestionVersionService {
    int insert(QuestionVersionModel versionModel);

    int update(QuestionVersionModel versionModel);

    int insertTag(Long version,Long tag);

    int insertTag(Long version, List<TagInfoModel> tags);

    int insertTag(Long version,TagInfoModel... tags);

    int deleteTag(Long version);

    Integer findVersionById(Long id);

    List<QuestionVersionModel> searchByQuestionId(Long questionId,Boolean hasContent);

    List<QuestionVersionModel> searchByQuestionId(Long questionId);

    QuestionVersionModel findById(Long id);

    QuestionVersionModel findByQuestionIdAndId(Long id,Long questionId);

    int updateVersion(Long questionId,Integer version,Long ip,Long user);

    int updateVersion(Long questionId,Integer version,Boolean isPublished,Long ip,Long user);

    int updateVersion(Long questionId, Integer version, Integer draft,Boolean isPublished,Long time,Long ip,Long user);

    int updateVersion(QuestionVersionModel versionModel);

    int updateDraft(Long questionId,Integer version,Boolean isPublished,Long time,Long ip,Long user);

    int updateDraft(Long questionId,Integer version,Long time,Long ip,Long user);

    int updateDraft(QuestionVersionModel versionModel);

    QuestionVersionModel addVersion(Long id, Long userId, String title, String content, String html, String description,
                                    RewardType rewardType, Double rewardValue, Double rewardAdd, List<TagInfoModel> tags, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;
}
