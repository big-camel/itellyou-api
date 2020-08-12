package com.itellyou.service.software;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.software.SoftwareVersionModel;

import java.util.HashSet;

public interface SoftwareDocService {

    Long create(Long userId, Long groupId, String name, String content, String html, String description, HashSet<Long> tagIds, String remark, String save_type, Long ip) throws Exception;

    Long create(Long userId, Long groupId, String name, String content, String html, String description, HashSet<Long> tagIds, String remark, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

    SoftwareVersionModel addVersion(Long id, Long userId, Long groupId, String name, String content, String html, String description, HashSet<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

}
