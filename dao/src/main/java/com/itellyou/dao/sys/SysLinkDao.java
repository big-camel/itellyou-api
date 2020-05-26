package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysLinkModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysLinkDao {
    int insert(SysLinkModel mode);

    int delete(Long id);

    List<SysLinkModel> search(@Param("id") Long id,
                              @Param("text") String text,
                              @Param("link") String link,
                              @Param("target") String target,
                              @Param("userId") Long userId,
                              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                              @Param("ip") Long ip,
                              @Param("order") Map<String, String> order,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    int count(@Param("id") Long id,
              @Param("text") String text,
              @Param("link") String link,
              @Param("target") String target,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
