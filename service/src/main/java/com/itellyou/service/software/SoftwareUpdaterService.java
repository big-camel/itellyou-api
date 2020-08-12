package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareUpdaterDetailModel;
import com.itellyou.model.software.SoftwareUpdaterModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

public interface SoftwareUpdaterService {
    int add(SoftwareUpdaterModel model);

    int addAll(HashSet<SoftwareUpdaterModel> updaterValues);

    int clear(Long releaseId);

    int remove(Long id);

    List<SoftwareUpdaterModel> findByReleaseId(Long releaseIds);

    List<SoftwareUpdaterDetailModel> search(HashSet<Long> releaseIds);
}
