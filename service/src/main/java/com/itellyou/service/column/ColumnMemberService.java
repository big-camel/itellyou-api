package com.itellyou.service.column;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnMemberDetailModel;
import com.itellyou.model.column.ColumnMemberModel;

import java.util.List;
import java.util.Map;

public interface ColumnMemberService {

    int insert(ColumnMemberModel model) throws Exception;
    int delete(Long columnId, Long userId) throws Exception;
    List<ColumnMemberDetailModel> search(Long columnId, Long userId,Long searchId,
                                         Long beginTime, Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
    int count(Long columnId, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<ColumnMemberDetailModel> page(Long columnId, Long userId,Long searchId,
                                          Long beginTime, Long endTime,
                                          Long ip,
                                          Map<String, String> order,
                                          Integer offset,
                                          Integer limit);
}
