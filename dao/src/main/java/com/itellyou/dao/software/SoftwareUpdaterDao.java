package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareReleaseModel;
import com.itellyou.model.software.SoftwareUpdaterModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface SoftwareUpdaterDao {
    int add(SoftwareUpdaterModel model);

    int addAll(Collection<SoftwareUpdaterModel> updaterValues);

    int clear(Long releaseId);

    int remove(Long id);

    List<SoftwareUpdaterModel> search(Collection<Long> releaseIds);
}
