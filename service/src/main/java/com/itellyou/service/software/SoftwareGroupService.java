package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareGroupModel;
import com.itellyou.model.sys.PageModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SoftwareGroupService {
    int add(SoftwareGroupModel model);

    int addAll(HashSet<SoftwareGroupModel> groupValues);

    int clear();

    int remove(Long id);

    List<SoftwareGroupModel> searchAll();

    SoftwareGroupModel searchById(Long id);

    List<SoftwareGroupModel> search(HashSet<Long> ids,  String name,  Long userId,Long beginTime, Long endTime,
                                    Long ip,
                                    Map<String, String> order, Integer offset, Integer limit);

    int count(HashSet<Long> ids,  String name,  Long userId,  Long beginTime,  Long endTime,
              Long ip);

    PageModel<SoftwareGroupModel> page(HashSet<Long> ids,  String name,  Long userId,Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order, Integer offset, Integer limit);
}
