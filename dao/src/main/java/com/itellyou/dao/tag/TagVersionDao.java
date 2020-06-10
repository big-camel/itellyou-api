package com.itellyou.dao.tag;

import com.itellyou.model.tag.TagVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TagVersionDao {
    int insert(TagVersionModel versionModel);

    int update(TagVersionModel versionModel);

    Integer findVersionById(Long id);

    List<TagVersionModel> search(@Param("ids") HashSet<Long> ids,
                                        @Param("tagMap") Map<Long,Integer> tagMap,
                                      @Param("userId") Long userId,
                                      @Param("hasContent") Boolean hasContent,
                                      @Param("isReviewed") Boolean isReview,
                                      @Param("isDisabled") Boolean isDisable,
                                      @Param("isPublished") Boolean isPublish,
                                      @Param("beginTime") Long beginTime,
                                      @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String,String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    int count(@Param("ids") HashSet<Long> ids,
              @Param("tagMap") Map<Long,Integer> tagMap,
                     @Param("userId") Long userId,
                     @Param("isReviewed") Boolean isReview,
                     @Param("isDisabled") Boolean isDisable,
                     @Param("isPublished") Boolean isPublish,
                     @Param("beginTime") Long beginTime,
                     @Param("endTime") Long endTime,
                     @Param("ip") Long ip);
}
