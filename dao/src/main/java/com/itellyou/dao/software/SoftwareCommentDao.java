package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.sys.VoteType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SoftwareCommentDao {
    int insert(SoftwareCommentModel commentModel);

    SoftwareCommentModel findById(Long id);

    List<SoftwareCommentModel> search(@Param("ids") Collection<Long> ids, @Param("softwareId") Long softwareId, @Param("parentIds") Collection<Long> parentIds, @Param("replyId") Long replyId, @Param("userId") Long userId,
                                     @Param("isDeleted") Boolean isDeleted,
                                     @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                                     @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                     @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                     @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                     @Param("ip") Long ip,
                                     @Param("order") Map<String, String> order,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids, @Param("softwareId") Long softwareId, @Param("parentIds") Collection<Long> parentIds, @Param("replyId") Long replyId, @Param("userId") Long userId,
              @Param("isDeleted") Boolean isDeleted,
              @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
              @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
              @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    List<SoftwareCommentModel> searchChild(@Param("ids") Collection<Long> ids, @Param("softwareId") Long softwareId, @Param("parentIds") Collection<Long> parentIds, @Param("replyId") Long replyId, @Param("userId") Long userId,
                                          @Param("isDeleted") Boolean isDeleted, @Param("childCount") Integer childCount,
                                          @Param("minComment") Integer minComment, @Param("maxComment") Integer maxComment,
                                          @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                          @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                          @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                          @Param("ip") Long ip,
                                          @Param("order") Map<String, String> order);

    int updateDeleted(@Param("id") Long id, @Param("isDeleted") Boolean isDeleted);

    int updateComments(@Param("id") Long id, @Param("value") Integer value);

    int updateVote(@Param("type") VoteType type, @Param("value") Integer value, @Param("id") Long id);
}
