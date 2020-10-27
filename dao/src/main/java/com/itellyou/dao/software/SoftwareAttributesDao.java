package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareAttributesModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface SoftwareAttributesDao {
    int add(SoftwareAttributesModel model);

    int addAll(Collection<SoftwareAttributesModel> attributesValues);

    int clear(Long softwareId);

    int remove(Long id);

    List<SoftwareAttributesModel> search(Collection<Long> softwareIds);
}
