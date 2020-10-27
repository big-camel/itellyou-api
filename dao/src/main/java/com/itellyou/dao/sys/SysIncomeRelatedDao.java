package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysIncomeRelatedModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysIncomeRelatedDao {

    int insertModels(@Param("models") SysIncomeRelatedModel... models);

    List<SysIncomeRelatedModel> search(@Param("ids") Collection<Long> ids,
                                      @Param("incomeId") Long incomeId,
                                       @Param("configId") Long configId,
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids,
              @Param("incomeId") Long incomeId,
              @Param("configId") Long configId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
