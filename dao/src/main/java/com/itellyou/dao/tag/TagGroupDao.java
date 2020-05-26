package com.itellyou.dao.tag;

import com.itellyou.model.tag.TagGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TagGroupDao {
    int insert(TagGroupModel groupModel);

    TagGroupModel findById(Long id);

    TagGroupModel findByName(String name);

    int updateTagCountById(@Param("id") Long id,@Param("step") Integer step);

    int updateNameById(@Param("id") Long id,@Param("name") String name);

    int deleteById(Long id);

    List<TagGroupModel> search(@Param("id") Long id,
                                @Param("userId") Long userId,
                                @Param("ip") Long ip,
                                @Param("isDisabled") Boolean isDisabled,
                                @Param("isPublished") Boolean isPublished,
                                @Param("minTagCount") Integer minTagCount, @Param("maxTagCount") Integer maxTagCount,
                                @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                @Param("order") Map<String,String> order,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    int count(@Param("id") Long id,@Param("userId") Long userId,
               @Param("ip") Long ip,
               @Param("minTagCount") Integer minTagCount, @Param("maxTagCount") Integer maxTagCount,
               @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);
}
