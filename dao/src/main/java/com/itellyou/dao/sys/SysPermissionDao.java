package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysPermissionMethod;
import com.itellyou.model.sys.SysPermissionModel;
import com.itellyou.model.sys.SysPermissionPlatform;
import com.itellyou.model.sys.SysPermissionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysPermissionDao {

    int insert(SysPermissionModel model);

    int delete(String name);

    int updateByName(SysPermissionModel model);

    SysPermissionModel findByName(String name);

    List<SysPermissionModel> search(@Param("userId") Long userId,@Param("platform")SysPermissionPlatform platform, @Param("type")SysPermissionType type, @Param("method") SysPermissionMethod method, @Param("name") String name, @Param("order") Map<String,String> order, @Param("offset") Integer offset, @Param("limit") Integer limit);

    int count(@Param("userId") Long userId,@Param("platform")SysPermissionPlatform platform, @Param("type")SysPermissionType type, @Param("method") SysPermissionMethod method, @Param("name") String name);
}
