package com.itellyou.service.column;

import com.itellyou.model.column.ColumnStarModel;

import java.util.Collection;
import java.util.List;

public interface ColumnStarSingleService {

    ColumnStarModel find(Long columnId, Long userId);

    List<ColumnStarModel> search(Collection<Long> columnIds, Long userId);
}
