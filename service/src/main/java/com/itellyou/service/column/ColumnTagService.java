package com.itellyou.service.column;

import com.itellyou.model.column.ColumnTagModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ColumnTagService {

    int add(ColumnTagModel model);

    int addAll(Long columnId, HashSet<Long> tagIds);

    int clear(Long columnId);

    int remove(Long columnId, Long tagId);

    Map<Long, List<ColumnTagModel>> searchTags(HashSet<Long> columnIds);

    HashSet<Long> searchTagId(HashSet<Long> columnIds);

    HashSet<Long> searchTagId(Long columnId);

    HashSet<Long> searchColumnId(Long tagId);

    HashSet<Long> searchColumnId(HashSet<Long> tagId);
}
