package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.sys.VoteType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionAnswerDao {
    int insert(QuestionAnswerModel answerModel);

    QuestionAnswerModel findByQuestionIdAndUserId(@Param("questionId") Long questionId,@Param("userId") Long userId);

    QuestionAnswerModel findById(Long id);

    List<QuestionAnswerDetailModel> search(@Param("ids") HashSet<Long> ids, @Param("questionId") Long questionId, @Param("mode") String mode, @Param("searchUserId") Long searchUserId, @Param("userId") Long userId,
                                           @Param("hasContent") Boolean hasContent,
                                           @Param("isAdopted") Boolean isAdopted,
                                           @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                                           @Param("ip") Long ip, @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                                           @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                                           @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                           @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                           @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                                           @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                           @Param("order") Map<String, String> order,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    int count(@Param("ids") HashSet<Long> ids,@Param("questionId") Long questionId, @Param("mode") String mode,@Param("searchUserId") Long searchUserId, @Param("userId") Long userId,
                    @Param("isAdopted") Boolean isAdopted,@Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                    @Param("ip") Long ip, @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                    @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                    @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                    @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                    @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar,
                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    List<Map<String,String>> groupByUserId(@Param("questionId") Long questionId,@Param("isAdopted") Boolean isAdopted,
                                    @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,@Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                    @Param("order") Map<String, String> order,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    int groupCountByUserId(@Param("questionId") Long questionId,@Param("isAdopted") Boolean isAdopted,
                           @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    int updateVersion(@Param("id") Long id, @Param("version") Integer version, @Param("draft") Integer draft, @Param("isPublished") Boolean isPublished,@Param("isDisabled") Boolean isDisabled,@Param("isDeleted") Boolean isDeleted, @Param("time") Long time, @Param("ip") Long ip, @Param("userId") Long userId);
    int updateView(@Param("id") Long id, @Param("view") Integer view);

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
}
