package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface SoftwareTagDao {
    int add(SoftwareTagModel model);

    int addAll(@Param("softwareId") Long softwareId, @Param("tagIds") HashSet<Long> tagIds);

    int clear(Long softwareId);

    int remove(@Param("softwareId") Long softwareId, @Param("tagId") Long tagId);

    List<SoftwareTagModel> searchTags(@Param("softwareIds") HashSet<Long> softwareIds);

    HashSet<Long> searchTagId(Long softwareId);

    HashSet<Long> searchSoftwareId(@Param("tagIds") HashSet<Long> tagIds);
}
