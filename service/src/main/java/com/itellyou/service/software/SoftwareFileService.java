package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareFileModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

public interface SoftwareFileService {
    int add(SoftwareFileModel model);

    int addAll(HashSet<SoftwareFileModel> fileValues);

    int clear(Long updaterId);

    int remove(Long id);

    int updateRecommendById(boolean isRecommend,Long id);

    List<SoftwareFileModel> search(HashSet<Long> updaterIds);
}
