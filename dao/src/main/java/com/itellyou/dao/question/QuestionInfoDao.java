package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionUpdateStepModel;
import com.itellyou.model.sys.RewardType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionInfoDao {
    int insert(QuestionInfoModel questionInfoModel);

    /**
     * 批量增加计数，请确保id必须已存在
     * @param models
     * @return
     */
    int addStep(@Param("models") QuestionUpdateStepModel... models);

    List<QuestionInfoModel> search(@Param("ids") Collection<Long> ids, @Param("mode") String mode, @Param("userId") Long userId,
                                     @Param("isDisabled") Boolean isDisabled, @Param("isAdopted") Boolean isAdopted, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                                     @Param("ip") Long ip,
                                     @Param("rewardType") RewardType rewardType,
                                     @Param("minRewardValue") Double minRewardValue, @Param("maxRewardValue") Double maxRewardValue,
                                     @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                                     @Param("minAnswer") Integer minAnswer, @Param("maxAnswer") Integer maxAnswer,
                                     @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                                     @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                     @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                     @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                                     @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                     @Param("order") Map<String,String> order,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    int count(@Param("ids") Collection<Long> ids,@Param("mode") String mode, @Param("userId") Long userId,
                    @Param("isDisabled") Boolean isDisabled,@Param("isAdopted") Boolean isAdopted, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                    @Param("ip") Long ip,
                    @Param("rewardType") RewardType rewardType,
                    @Param("minRewardValue") Double minRewardValue, @Param("maxRewardValue") Double maxRewardValue,
                    @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                    @Param("minAnswer") Integer minAnswer, @Param("maxAnswer") Integer maxAnswer,
                    @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                    @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                    @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                    @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    int updateVersion(@Param("id") Long id,@Param("version") Integer version,@Param("draft") Integer draft,@Param("isPublished") Boolean isPublished,@Param("time") Long time,@Param("ip") Long ip,@Param("userId") Long userId);
    int updateView(@Param("id") Long id,@Param("viewCount") Integer viewCount);
    int updateAnswers(@Param("id") Long id,@Param("value") Integer value);
    int updateAdopt(@Param("isAdopted") Boolean isAdopted,@Param("adoptionId") Long adoptionId,@Param("id") Long id);

    QuestionInfoModel findById(Long id);

    int updateComments(@Param("id") Long id, @Param("value") Integer value);
    int updateStarCountById(@Param("id") Long id,@Param("step") Integer step);
    int updateDeleted(@Param("deleted") boolean deleted, @Param("id") Long id);
    int updateMetas(@Param("id") Long id,@Param("cover") String cover);

    int updateInfo(@Param("id") Long id,
                   @Param("title") String title,
                   @Param("description") String description,
                   @Param("rewardType") RewardType rewardType,
                   @Param("rewardAdd") Double rewardAdd,
                   @Param("rewardValue") Double rewardValue,
                   @Param("time") Long time,
                   @Param("ip") Long ip,
                   @Param("userId") Long userId);
}
