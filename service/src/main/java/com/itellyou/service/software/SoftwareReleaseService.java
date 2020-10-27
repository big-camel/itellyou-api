package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareReleaseDetailModel;
import com.itellyou.model.software.SoftwareReleaseModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

public interface SoftwareReleaseService {
    int add(SoftwareReleaseModel model);

    int addAll(Collection<SoftwareReleaseModel> releaseValues);

    int clear(Long softwareId);

    int remove(Long id);

    List<SoftwareReleaseDetailModel> search(Collection<Long> softwareIds);
}
