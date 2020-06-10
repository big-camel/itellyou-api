package com.itellyou.service.tag;

import com.itellyou.model.tag.TagVersionModel;

public interface TagDocService {

    Long create(Long userId,String name, String content, String html,String icon, String description, String remark, String save_type, Long ip) throws Exception;

    TagVersionModel addVersion(Long id, Long userId, String content, String html, String icon, String description, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

}
