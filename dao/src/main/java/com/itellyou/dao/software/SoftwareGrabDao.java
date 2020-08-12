package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareGrabModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SoftwareGrabDao {
    SoftwareGrabModel findById(String id);

    int update(SoftwareGrabModel model);
}
