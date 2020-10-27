package com.itellyou.dao.question;

import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerTotalModel;
import com.itellyou.model.sys.VoteType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionAnswerDao {
    int insert(QuestionAnswerModel answerModel);

    int addStep(@Param("models") DataUpdateStepModel... models);

    QuestionAnswerModel findById(Long id);

    List<QuestionAnswerModel> search(@Param("ids") Collection<Long> ids, @Param("questionIds") Collection<Long> questionIds, @Param("mode") String mode,@Param("userId") Long userId,
                                           @Param("isAdopted") Boolean isAdopted,
                                           @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                                           @Param("ip") Long ip, @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                                           @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                                           @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                           @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                           @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                                           @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                           @Param("order") Map<String, String> order,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids, @Param("questionIds") Collection<Long> questionIds, @Param("mode") String mode, @Param("userId") Long userId,
                    @Param("isAdopted") Boolean isAdopted,@Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                    @Param("ip") Long ip, @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                    @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                    @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                    @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                    @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    List<QuestionAnswerModel> searchChild(@Param("ids") Collection<Long> ids, @Param("questionIds") Collection<Long> questionIds, @Param("mode") String mode, @Param("userId") Long userId,
                                          @Param("childCount") Integer childCount,
                                          @Param("isAdopted") Boolean isAdopted,
                                          @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                                          @Param("ip") Long ip, @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                                          @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                                          @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                          @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                          @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                                          @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                          @Param("order") Map<String, String> order);

    List<QuestionAnswerTotalModel> totalByUser(@Param("userIds") Collection<Long> userIds,@Param("questionId") Long questionId, @Param("isAdopted") Boolean isAdopted,
                                                 @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                                 @Param("order") Map<String, String> order,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);

    int updateVersion(@Param("id") Long id, @Param("version") Integer version, @Param("draft") Integer draft, @Param("isPublished") Boolean isPublished,@Param("isDisabled") Boolean isDisabled,@Param("isDeleted") Boolean isDeleted, @Param("time") Long time, @Param("ip") Long ip, @Param("userId") Long userId);
    int updateView(@Param("id") Long id, @Param("viewCount") Integer viewCount);

    int updateComments(@Param("id") Long id, @Param("value") Integer value);

    /**
     * 更新禁用状态
     * @param isDisabled
     * @param id
     * @return
     */
    int updateDisabled(@Param("isDisabled") Boolean isDisabled,@Param("id") Long id);

    /**
     * 更新删除状态
     * @param isDeleted
     * @param id
     * @return
     */
    int updateDeleted(@Param("isDeleted") Boolean isDeleted,@Param("id") Long id);

    int updateAdopted(@Param("isAdopted") Boolean isAdopted,@Param("id") Long id);

    int updateVote(@Param("type") VoteType type, @Param("value") Integer value, @Param("id") Long id);

    int updateStarCountById(@Param("id") Long id,@Param("step") Integer step);

    int updateMetas(@Param("id") Long id,@Param("cover") String cover);

    int updateInfo(@Param("id") Long id,
                   @Param("description") String description,
                   @Param("time") Long time,
                   @Param("ip") Long ip,
                   @Param("userId") Long userId);
}
