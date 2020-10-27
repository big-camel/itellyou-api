package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SoftwareVersionDao {
    int insert(SoftwareVersionModel versionModel);

    int update(SoftwareVersionModel versionModel);

    Integer findVersionById(Long id);

    List<SoftwareVersionModel> search(@Param("ids") Collection<Long> ids,
                                     @Param("softwareMap") Map<Long, Integer> softwareMap,
                                     @Param("userId") Long userId,
                                     @Param("groupId") Long groupId,
                                     @Param("hasContent") Boolean hasContent,
                                     @Param("isReviewed") Boolean isReview,
                                     @Param("isDisabled") Boolean isDisable,
                                     @Param("isPublished") Boolean isPublish,
                                     @Param("beginTime") Long beginTime,
                                     @Param("endTime") Long endTime,
                                     @Param("ip") Long ip,
                                     @Param("order") Map<String, String> order,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    Integer count(@Param("ids") Collection<Long> ids,
                  @Param("softwareMap") Map<Long, Integer> softwareMap,
                  @Param("userId") Long userId,
                  @Param("groupId") Long groupId,
                  @Param("isReviewed") Boolean isReview,
                  @Param("isDisabled") Boolean isDisable,
                  @Param("isPublished") Boolean isPublish,
                  @Param("beginTime") Long beginTime,
                  @Param("endTime") Long endTime,
                  @Param("ip") Long ip);
}
