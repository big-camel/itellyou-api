package com.itellyou.dao.column;

import com.itellyou.model.column.ColumnInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ColumnInfoDao {
    int insert(ColumnInfoModel infoModel);

    int insertTag(@Param("columnId") Long columnId, @Param("tags") Long... tags);

    int deleteTag(Long columnId);

    List<ColumnInfoModel> search(@Param("ids") HashSet<Long> ids, @Param("name") String name, @Param("userId") Long userId,@Param("memberId") Long memberId, @Param("searchUserId") Long searchUserId,
                                   @Param("isDisabled") Boolean isDisabled, @Param("isReviewed") Boolean isReviewed, @Param("isDeleted") Boolean isDeleted,
                                   @Param("minArticles") Integer minArticles, @Param("maxArticles") Integer maxArticles,
                                   @Param("minStars") Integer minStars, @Param("maxStars") Integer maxStars,
                                   @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                   @Param("ip") Long ip,
                                   @Param("order") Map<String, String> order,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);

    int count(@Param("ids") HashSet<Long> ids,@Param("name") String name, @Param("userId") Long userId,@Param("memberId") Long memberId,
                    @Param("isDisabled") Boolean isDisabled, @Param("isReviewed") Boolean isReviewed, @Param("isDeleted") Boolean isDeleted,
                    @Param("minArticles") Integer minArticles, @Param("maxArticles") Integer maxArticles,
                    @Param("minStars") Integer minStars, @Param("maxStars") Integer maxStars,
                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,@Param("ip") Long ip);

    ColumnInfoModel findById(Long id);

    ColumnInfoModel findByName(String name);

    int updateArticles(@Param("id") Long id, @Param("step") Integer value);

    int updateStars(@Param("id") Long id, @Param("step") Integer value);

    int updateMemberCount(@Param("id") Long id, @Param("step") Integer step);

    int update(ColumnInfoModel model);

    int updateDeleted(@Param("deleted") boolean deleted, @Param("id") Long id);
}
