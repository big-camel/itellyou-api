package com.itellyou.dao.column;

import com.itellyou.model.column.ColumnTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface ColumnTagDao {
    int add(ColumnTagModel model);

    int addAll(@Param("columnId") Long columnId, @Param("tagIds") Collection<Long> tagIds);

    int clear(Long columnId);

    int remove(@Param("columnId") Long columnId, @Param("tagId") Long tagId);

    List<ColumnTagModel> searchTags(@Param("columnIds")Collection<Long> columnIds);

    List<ColumnTagModel> searchColumns(@Param("tagIds") Collection<Long> tagIds);
}
