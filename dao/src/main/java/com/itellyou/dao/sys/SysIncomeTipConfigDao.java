package com.itellyou.dao.sys;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.SysIncomeTipConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysIncomeTipConfigDao {

    int insert(SysIncomeTipConfigModel model);

    List<SysIncomeTipConfigModel> search(@Param("ids") Collection<Long> ids,
                                      @Param("name") String name,
                                      @Param("dataType") EntityType dataType,
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids,
              @Param("name") String name,
              @Param("dataType") EntityType dataType,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    int updateById(SysIncomeTipConfigModel model);

    int deleteById(Long id);
}
