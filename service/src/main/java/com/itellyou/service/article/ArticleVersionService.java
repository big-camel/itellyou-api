package com.itellyou.service.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.tag.TagInfoModel;

import java.util.List;

public interface ArticleVersionService {
    int insert(ArticleVersionModel versionModel);

    int update(ArticleVersionModel versionModel);

    int insertTag(Long version, Long tag);

    int insertTag(Long version, List<TagInfoModel> tags);

    int insertTag(Long version, TagInfoModel... tags);

    int deleteTag(Long version);

    Integer findVersionById(Long id);

    List<ArticleVersionModel> searchByArticleId(Long articleId, Boolean hasContent);

    List<ArticleVersionModel> searchByArticleId(Long articleId);

    ArticleVersionModel findById(Long id);

    ArticleVersionModel findByArticleIdAndId(Long id, Long articleId);

    int updateVersion(Long articleId, Integer version, Long ip, Long user);

    int updateVersion(Long articleId, Integer version, Boolean isPublished, Long ip, Long user);

    int updateVersion(Long articleId, Integer version, Integer draft, Boolean isPublished, Long time, Long ip, Long user);

    int updateVersion(ArticleVersionModel versionModel);

    int updateDraft(Long articleId, Integer version, Boolean isPublished, Long time, Long ip, Long user);

    int updateDraft(Long articleId, Integer version, Long time, Long ip, Long user);

    int updateDraft(ArticleVersionModel versionModel);

    ArticleVersionModel addVersion(Long id, Long userId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, List<TagInfoModel> tags, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

}
