package com.itellyou.dao.column;

import com.itellyou.model.column.ColumnTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface ColumnTagDao {
    int add(ColumnTagModel model);

    int addAll(@Param("columnId") Long columnId, @Param("tagIds") HashSet<Long> tagIds);

    int clear(Long columnId);

    int remove(@Param("columnId") Long columnId, @Param("tagId") Long tagId);

    List<ColumnTagModel> searchTags(@Param("columnIds")HashSet<Long> columnIds);

    HashSet<Long> searchTagId(@Param("columnIds") HashSet<Long> columnIds);

    HashSet<Long> searchColumnId(@Param("tagIds") HashSet<Long> tagIds);
}
