package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionVersionTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface QuestionVersionTagDao {
    int add(QuestionVersionTagModel model);

    int addAll(@Param("versionId") Long versionId, @Param("tagIds") HashSet<Long> tagIds);

    int clear(Long versionId);

    int remove(@Param("versionId") Long versionId, @Param("tagId") Long tagId);

    List<QuestionVersionTagModel> searchTags(@Param("versionIds") HashSet<Long> versionIds);

    HashSet<Long> searchTagId(Long versionId);
}
