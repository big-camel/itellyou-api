package com.itellyou.service.column;

import com.itellyou.model.column.ColumnTagModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ColumnTagService {

    int add(ColumnTagModel model);

    int addAll(Long columnId, Collection<Long> tagIds);

    int clear(Long columnId);

    int remove(Long columnId, Long tagId);

    Map<Long, List<ColumnTagModel>> searchTags(Collection<Long> columnIds);

    Map<Long, List<ColumnTagModel>> searchColumns(Collection<Long> tagIds);

    Collection<Long> searchColumnIds(Collection<Long> tagId);


}
