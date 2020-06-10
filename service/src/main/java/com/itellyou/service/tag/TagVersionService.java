package com.itellyou.service.tag;

import com.itellyou.model.tag.TagVersionModel;

public interface TagVersionService {
    int insert(TagVersionModel versionModel);

    int update(TagVersionModel versionModel);

    int updateVersionById(Long id,Integer version,Integer draft,Boolean isPublished,Long time,Long ip,Long userId);

    int updateVersion(Long id,Integer version,Boolean isPublished,Long time,Long ip,Long userId);

    int updateVersion(TagVersionModel versionModel);

    int updateDraft(Long id,Integer draft,Boolean isPublished,Long time,Long ip,Long userId);

    int updateDraft(TagVersionModel versionModel);


}
