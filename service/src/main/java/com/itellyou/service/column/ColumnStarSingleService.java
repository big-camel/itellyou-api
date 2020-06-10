package com.itellyou.service.column;

import com.itellyou.model.column.ColumnStarModel;

import java.util.HashSet;
import java.util.List;

public interface ColumnStarSingleService {

    ColumnStarModel find(Long columnId, Long userId);

    List<ColumnStarModel> search(HashSet<Long> columnIds, Long userId);
}
