package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareGrabModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

public interface SoftwareGrabService {
    SoftwareGrabModel findById(String id);

    int update(SoftwareGrabModel model);


}
