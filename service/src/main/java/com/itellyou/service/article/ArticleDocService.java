package com.itellyou.service.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;

import java.util.Collection;

public interface ArticleDocService {

    Long create(Long userId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, Collection<Long> tagIds, String remark, String save_type, Long ip) throws Exception;

    ArticleVersionModel addVersion(Long id, Long userId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, Collection<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

}
