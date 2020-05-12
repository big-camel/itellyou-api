package com.itellyou.dao.common;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.common.ViewInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ViewInfoDao {
    int insert(ViewInfoModel viewModel);

    List<ViewInfoModel> search(@Param("id") Long id, @Param("userId") Long userId, @Param("dataType") EntityType dataType, @Param("dataKey") Long dataKey, @Param("os")  String os, @Param("browser")  String browser, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("ip") Long ip,
                               @Param("order") Map<String,String> order,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    int count(@Param("id") Long id,@Param("userId") Long userId,@Param("dataType") EntityType dataType,@Param("dataKey") Long dataKey,@Param("os")  String os,@Param("browser")  String browser, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,@Param("ip") Long ip);

    int update(ViewInfoModel viewModel);
}
