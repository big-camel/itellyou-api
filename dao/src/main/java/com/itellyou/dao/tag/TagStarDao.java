package com.itellyou.dao.tag;

import com.itellyou.model.column.ColumnStarDetailModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TagStarDao {
    int insert(TagStarModel model);
    int delete(@Param("tagId") Long tagId, @Param("userId") Long userId);
    List<TagStarDetailModel> search(@Param("tagId") Long tagId,
                                    @Param("userId") Long userId,
                                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                    @Param("ip") Long ip,
                                    @Param("order") Map<String, String> order,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);
    int count(@Param("tagId") Long tagId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
