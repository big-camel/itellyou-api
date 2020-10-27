package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareFileModel;
import com.itellyou.model.software.SoftwareInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface SoftwareFileDao {
    int add(SoftwareFileModel model);

    int addAll(Collection<SoftwareFileModel> fileValues);

    int clear(Long updaterId);

    int remove(Long id);

    int updateRecommendById(@Param("isRecommend") boolean isRecommend,@Param("id") Long id);

    List<SoftwareFileModel> search(Collection<Long> updaterIds);
}
