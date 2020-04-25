package com.itellyou.service.column;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnStarDetailModel;
import com.itellyou.model.column.ColumnStarModel;

import java.util.List;
import java.util.Map;

public interface ColumnStarService {
    int insert(ColumnStarModel model) throws Exception;
    int delete(Long columnId, Long userId) throws Exception;
    List<ColumnStarDetailModel> search(Long columnId, Long userId,
                                       Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order,
                                       Integer offset,
                                       Integer limit);
    int count(Long columnId, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<ColumnStarDetailModel> page(Long columnId, Long userId,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);
}
