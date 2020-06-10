package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionModel;

public interface QuestionVersionService {
    int insert(QuestionVersionModel versionModel);

    int update(QuestionVersionModel versionModel);

    int updateVersion(Long questionId,Integer version,Long ip,Long user);

    int updateVersion(Long questionId,Integer version,Boolean isPublished,Long ip,Long user);

    int updateVersion(Long questionId, Integer version, Integer draft,Boolean isPublished,Long time,Long ip,Long user);

    int updateVersion(QuestionVersionModel versionModel);

    int updateDraft(Long questionId,Integer version,Boolean isPublished,Long time,Long ip,Long user);

    int updateDraft(Long questionId,Integer version,Long time,Long ip,Long user);

    int updateDraft(QuestionVersionModel versionModel);

}
