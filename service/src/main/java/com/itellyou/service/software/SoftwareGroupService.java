package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareGroupModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareGroupService {
    int add(SoftwareGroupModel model);

    int addAll(Collection<SoftwareGroupModel> groupValues);

    int clear();

    int remove(Long id);

    List<SoftwareGroupModel> searchAll();

    SoftwareGroupModel searchById(Long id);

    List<SoftwareGroupModel> search(Collection<Long> ids,  String name,  Long userId,Long beginTime, Long endTime,
                                    Long ip,
                                    Map<String, String> order, Integer offset, Integer limit);

    int count(Collection<Long> ids,  String name,  Long userId,  Long beginTime,  Long endTime,
              Long ip);

    PageModel<SoftwareGroupModel> page(Collection<Long> ids,  String name,  Long userId,Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order, Integer offset, Integer limit);
}
