package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareReleaseModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface SoftwareReleaseDao {
    int add(SoftwareReleaseModel model);

    int addAll(HashSet<SoftwareReleaseModel> releaseValues);

    int clear(Long softwareId);

    int remove(Long id);

    List<SoftwareReleaseModel> search(HashSet<Long> softwareIds);
}
