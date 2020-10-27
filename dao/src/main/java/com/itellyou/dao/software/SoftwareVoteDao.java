package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface SoftwareVoteDao {
    int insert(SoftwareVoteModel voteModel);

    int deleteBySoftwareIdAndUserId(@Param("softwareId") Long softwareId, @Param("userId") Long userId);

    List<SoftwareVoteModel> search(@Param("softwareIds") Collection<Long> softwareIds, @Param("userId") Long userId);
}
