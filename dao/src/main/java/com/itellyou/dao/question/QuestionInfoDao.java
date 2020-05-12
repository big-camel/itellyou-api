package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.RewardType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionInfoDao {
    int insert(QuestionInfoModel questionInfoModel);

    List<QuestionDetailModel> search(@Param("ids") HashSet<Long> ids, @Param("mode") String mode, @Param("userId") Long userId, @Param("searchUserId") Long searchUserId,
                                     @Param("hasContent") Boolean hasContent,
                                     @Param("isDisabled") Boolean isDisabled, @Param("isAdopted") Boolean isAdopted, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                                     @Param("ip") Long ip,
                                     @Param("childCount") Integer childCount,
                                     @Param("rewardType") RewardType rewardType,
                                     @Param("minRewardValue") Double minRewardValue, @Param("maxRewardValue") Double maxRewardValue,
                                     @Param("tags") List<Long> tags,
                                     @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                                     @Param("minAnswers") Integer minAnswers, @Param("maxAnswers") Integer maxAnswers,
                                     @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                                     @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                     @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                     @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                                     @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                     @Param("order") Map<String,String> order,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    int count(@Param("ids") HashSet<Long> ids,@Param("mode") String mode, @Param("userId") Long userId,
                    @Param("isDisabled") Boolean isDisabled,@Param("isAdopted") Boolean isAdopted, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                    @Param("ip") Long ip,
                    @Param("rewardType") RewardType rewardType,
                    @Param("minRewardValue") Double minRewardValue, @Param("maxRewardValue") Double maxRewardValue,
                    @Param("tags") List<Long> tags,
                    @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                    @Param("minAnswers") Integer minAnswers, @Param("maxAnswers") Integer maxAnswers,
                    @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                    @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                    @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                    @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    int updateVersion(@Param("id") Long id,@Param("version") Integer version,@Param("draft") Integer draft,@Param("isPublished") Boolean isPublished,@Param("time") Long time,@Param("ip") Long ip,@Param("userId") Long userId);
    int updateView(@Param("id") Long id,@Param("view") Integer view);
    int updateAnswers(@Param("id") Long id,@Param("value") Integer value);
    int updateAdopt(@Param("isAdopted") Boolean isAdopted,@Param("adoptionId") Long adoptionId,@Param("id") Long id);

    QuestionInfoModel findById(Long id);

    int updateComments(@Param("id") Long id, @Param("value") Integer value);
    int updateStarCountById(@Param("id") Long id,@Param("step") Integer step);
    int updateDeleted(@Param("deleted") boolean deleted, @Param("id") Long id);
    int updateMetas(@Param("id") Long id,@Param("cover") String cover);
}
