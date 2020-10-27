package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleVersionTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface ArticleVersionTagDao {
    int add(ArticleVersionTagModel model);

    int addAll(@Param("versionId") Long versionId, @Param("tagIds") Collection<Long> tagIds);

    int clear(Long versionId);

    int remove(@Param("versionId") Long versionId, @Param("tagId") Long tagId);

    List<ArticleVersionTagModel> searchTags(@Param("versionIds") Collection<Long> versionIds);
}
