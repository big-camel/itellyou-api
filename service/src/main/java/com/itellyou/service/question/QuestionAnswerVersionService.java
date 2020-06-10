package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;

public interface QuestionAnswerVersionService {
    int insert(QuestionAnswerVersionModel versionModel);

    int updateVersion(Long id, Integer version, Long ip, Long user);

    int updateVersion(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long ip, Long user);

    int updateVersion(Long id, Integer version, Integer draft, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user);

    int updateVersion(QuestionAnswerVersionModel versionModel);

    int updateDraft(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user);

    int updateDraft(Long id, Integer version, Long time, Long ip, Long user);

    int updateDraft(QuestionAnswerVersionModel versionModel);

}
