package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysAdSlotModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysAdSlotDao {
    int insert(SysAdSlotModel model);

    int updateById(SysAdSlotModel model);

    int deleteById(@Param("id") long id);

    int deleteByAdId(@Param("adId") long adId);

    List<SysAdSlotModel> search(@Param("ids") Collection<Long> ids,
                            @Param("name") String name,
                            @Param("adId") Long adId,
                            @Param("userId") Long userId,
                            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                            @Param("ip") Long ip,
                            @Param("order") Map<String, String> order,
                            @Param("offset") Integer offset,
                            @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids,
              @Param("name") String name,
              @Param("adId") Long adId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
