package com.itellyou.service.tag;

import com.itellyou.model.tag.TagVersionModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagVersionService {
    int insert(TagVersionModel versionModel);

    int update(TagVersionModel versionModel);

    Integer findVersionById(Long id);

    List<TagVersionModel> searchByTagId(Long tagId, Boolean hasContent);

    List<TagVersionModel> searchByTagId(Long tagId);

    int count(Long id,
              Long tagId,
              String userId,
              Boolean isReview,
              Boolean isDisable,
              Boolean isPublish,
              Long beginTime,
              Long endTime,
              Long ip);

    TagVersionModel findById(Long id);

    TagVersionModel findByTagIdAndId(Long id,Long tagId);

    int updateVersionById(Long id,Integer version,Integer draft,Boolean isPublished,Long time,Long ip,Long userId);

    int updateVersion(Long id,Integer version,Boolean isPublished,Long time,Long ip,Long userId);

    int updateVersion(TagVersionModel versionModel);

    int updateDraft(Long id,Integer draft,Boolean isPublished,Long time,Long ip,Long userId);

    int updateDraft(TagVersionModel versionModel);

    TagVersionModel addVersion(Long id, Long userId, String content, String html,String icon, String description, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

}
