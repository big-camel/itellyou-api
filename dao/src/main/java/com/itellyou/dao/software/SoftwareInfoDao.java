package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.sys.VoteType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SoftwareInfoDao {
    int insert(SoftwareInfoModel infoModel);

    List<SoftwareInfoModel> search(@Param("ids") HashSet<Long> ids, @Param("mode") String mode, @Param("groupId") Long groupId, @Param("userId") Long userId,
                                  @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
                                  @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
                                  @Param("minView") Integer minView, @Param("maxView") Integer maxView,
                                   @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
                                   @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
                                  @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                  @Param("ip") Long ip,
                                  @Param("order") Map<String, String> order,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    int count(@Param("ids") HashSet<Long> ids, @Param("mode") String mode, @Param("groupId") Long groupId, @Param("userId") Long userId,
              @Param("isDisabled") Boolean isDisabled, @Param("isPublished") Boolean isPublished, @Param("isDeleted") Boolean isDeleted,
              @Param("minComments") Integer minComments, @Param("maxComments") Integer maxComments,
              @Param("minView") Integer minView, @Param("maxView") Integer maxView,
              @Param("minSupport") Integer minSupport, @Param("maxSupport") Integer maxSupport,
              @Param("minOppose") Integer minOppose, @Param("maxOppose") Integer maxOppose,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("ip") Long ip);

    int updateVersion(@Param("id") Long id, @Param("version") Integer version, @Param("draft") Integer draft, @Param("isPublished") Boolean isPublished, @Param("time") Long time, @Param("ip") Long ip, @Param("userId") Long userId);
    int updateView(@Param("id") Long id, @Param("view") Integer view);

    SoftwareInfoModel findById(Long id);

    int updateComments(@Param("id") Long id, @Param("value") Integer value);

    int updateVote(@Param("type") VoteType type, @Param("value") Integer value, @Param("id") Long id);

    int updateMetas(@Param("id") Long id, @Param("customDescription") String customDescription, @Param("logo") String logo);

    int updateDeleted(@Param("deleted") boolean deleted, @Param("id") Long id);

    int updateInfo(@Param("id") Long id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("groupId") Long groupId,
                   @Param("time") Long time,
                   @Param("ip") Long ip,
                   @Param("userId") Long userId);
}
