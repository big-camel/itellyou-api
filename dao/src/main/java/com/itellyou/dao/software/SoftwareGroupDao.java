package com.itellyou.dao.software;

import com.itellyou.model.software.SoftwareGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SoftwareGroupDao {
    int add(SoftwareGroupModel model);

    int addAll(Collection<SoftwareGroupModel> groupValues);

    int clear();

    int remove(Long id);

    List<SoftwareGroupModel> searchAll();

    List<SoftwareGroupModel> search(@Param("ids") Collection<Long> ids,@Param("name") String name,@Param("userId") Long userId, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                    @Param("ip") Long ip,
                                    @Param("order") Map<String, String> order,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    int count(@Param("ids") Collection<Long> ids,@Param("name") String name,@Param("userId") Long userId, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
