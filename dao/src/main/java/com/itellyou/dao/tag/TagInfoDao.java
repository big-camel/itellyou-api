package com.itellyou.dao.tag;

import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TagInfoDao {

    int insert(TagInfoModel tagInfoModel);

    int exists(@Param("ids") Long... ids);

    List<TagDetailModel> search(@Param("ids") HashSet<Long> ids,
                                @Param("name") String name,
                                @Param("mode") String mode,
                                @Param("groupId") Long groupId,
                                @Param("userId") Long userId,
                                @Param("searchUserId") Long searchUserId,
                                @Param("hasContent") Boolean hasContent,
                                @Param("isDisabled") Boolean isDisabled,
                                @Param("isPublished") Boolean isPublished,
                                @Param("ip") Long ip,
                                @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                                @Param("minQuestion") Integer minQuestion, @Param("maxQuestion") Integer maxQuestion,
                                @Param("minArticle") Integer minArticle, @Param("maxArticle") Integer maxArticle,
                                @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                @Param("order") Map<String,String> order,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    int count(@Param("ids") HashSet<Long> ids,
            @Param("name") String name,
            @Param("mode") String mode,
            @Param("groupId") Long groupId,
            @Param("userId") Long userId,
            @Param("isDisabled") Boolean isDisabled,
            @Param("isPublished") Boolean isPublished,
            @Param("ip") Long ip,
            @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
            @Param("minQuestion") Integer minQuestion, @Param("maxQuestion") Integer maxQuestion,
            @Param("minArticle") Integer minArticle, @Param("maxArticle") Integer maxArticle,
            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    TagInfoModel findById(Long id);

    TagInfoModel findByName(String name);

    int updateStarCountById(@Param("ids") List<Long> ids,@Param("step") Integer step);

    int updateArticleCountById(@Param("ids") List<Long> ids,@Param("step") Integer step);

    int updateQuestionCountById(@Param("ids") List<Long> ids,@Param("step") Integer step);

    int updateVersionById(@Param("id") Long id,@Param("version") Integer version,@Param("draft") Integer draft,@Param("isPublished") Boolean isPublished,@Param("time") Long time,@Param("ip") Long ip,@Param("userId") Long userId);

}
