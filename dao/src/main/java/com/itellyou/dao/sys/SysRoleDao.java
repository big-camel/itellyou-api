package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysRoleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysRoleDao {

    int insert(SysRoleModel model);

    SysRoleModel findByName(@Param("name") String name,@Param("userId") Long userId);

    SysRoleModel findById(Long id);

    int delete(@Param("id") Long id,@Param("userId") Long userId);

    int update(@Param("id") Long id,@Param("name") String name,@Param("disabled") Boolean disabled,@Param("description") String description);

    List<SysRoleModel> search(@Param("id") Long id,
                             @Param("name") String name,
                             @Param("disabled") Boolean disabled,
                              @Param("system") Boolean system,
                             @Param("userId") Long userId,
                             @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                             @Param("ip") Long ip,
                             @Param("order") Map<String, String> order,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    int count(@Param("id") Long id,
              @Param("name") String name,
              @Param("disabled") Boolean disabled,
              @Param("system") Boolean system,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
