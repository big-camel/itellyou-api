package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareReleaseDetailModel;
import com.itellyou.model.software.SoftwareReleaseModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

public interface SoftwareReleaseService {
    int add(SoftwareReleaseModel model);

    int addAll(HashSet<SoftwareReleaseModel> releaseValues);

    int clear(Long softwareId);

    int remove(Long id);

    List<SoftwareReleaseDetailModel> search(HashSet<Long> softwareIds);
}
