package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareCommentVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface SoftwareCommentVoteDao {
    int insert(SoftwareCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    List<SoftwareCommentVoteModel> search(@Param("commentIds") HashSet<Long> commentIds, @Param("userId") Long userId);
}
