package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareAttributesModel;
import com.itellyou.model.software.SoftwareInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

public interface SoftwareAttributesService {
    int add(SoftwareAttributesModel model);

    int addAll(HashSet<SoftwareAttributesModel> attributesValues);

    int clear(Long softwareId);

    int remove(Long id);

    List<SoftwareAttributesModel> search(HashSet<Long> softwareIds);
}
