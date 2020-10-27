package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysIncomeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysIncomeDao {

    int insertOrUpdate(SysIncomeModel model);

    List<SysIncomeModel> search(@Param("ids") Collection<Long> ids,
                                @Param("userId") Long userId,
                                @Param("beginDate") Long beginDate, @Param("endDate") Long endDate,
                                @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                @Param("ip") Long ip,
                                @Param("order") Map<String, String> order,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids,
              @Param("userId") Long userId,
              @Param("beginDate") Long beginDate, @Param("endDate") Long endDate,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
