package com.itellyou.dao.sys;

import com.itellyou.model.sys.AdType;
import com.itellyou.model.sys.SysAdModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysAdDao {
    int insert(SysAdModel model);

    List<SysAdModel> search(@Param("ids") Collection<Long> ids,
                            @Param("type") AdType type,
                                      @Param("name") String name,
                                      @Param("enabledForeign") Boolean enabledForeign,
                            @Param("enabledCn") Boolean enabledCn,
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids,
              @Param("type") AdType type,
              @Param("name") String name,
              @Param("enabledForeign") Boolean enabledForeign,
              @Param("enabledCn") Boolean enabledCn,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    int updateById(SysAdModel model);

    int updateEnabledForeignAll(@Param("enabled") boolean enabled);

    int updateEnabledCnAll(@Param("enabled") boolean enabled);

    int deleteById(@Param("id") long id);
}
