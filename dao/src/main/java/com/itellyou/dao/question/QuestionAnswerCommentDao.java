package com.itellyou.dao.question;

import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.sys.VoteType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionAnswerCommentDao {
    int insert(QuestionAnswerCommentModel commentModel);

    QuestionAnswerCommentModel findById(Long id);

    List<QuestionAnswerCommentModel> search(@Param("ids") HashSet<Long> ids, @Param("answerId") Long answerId, @Param("parentIds") HashSet<Long> parentIds, @Param("replyId") Long replyId, @Param("userId") Long userId,
                                                  @Param("isDeleted") Boolean isDeleted,
                                                  @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                                                  @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                                  @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                                  @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                                  @Param("ip") Long ip,
                                                  @Param("order") Map<String, String> order,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit);

    int count(@Param("ids") HashSet<Long> ids, @Param("answerId") Long answerId, @Param("parentIds") HashSet<Long> parentIds, @Param("replyId") Long replyId, @Param("userId") Long userId,
                    @Param("isDeleted") Boolean isDeleted,
                    @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                    @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                    @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                    @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                    @Param("ip") Long ip);

    List<QuestionAnswerCommentModel> searchChild(@Param("ids") HashSet<Long> ids, @Param("answerId") Long answerId, @Param("parentIds") HashSet<Long> parentIds, @Param("replyId") Long replyId, @Param("userId") Long userId,
                                                 @Param("isDeleted") Boolean isDeleted, @Param("childCount") Integer childCount,
                                                 @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                                                 @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                                 @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                                 @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                                 @Param("ip") Long ip,
                                                 @Param("order") Map<String, String> order);

    int updateDeleted(@Param("id") Long id,@Param("isDeleted") Boolean isDeleted);

    int updateComments(@Param("id") Long id,@Param("value") Integer value);

    int updateVote(@Param("type") VoteType type, @Param("value") Integer value, @Param("id") Long id);
}
