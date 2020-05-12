package com.itellyou.dao.thirdparty;

import com.itellyou.model.thirdparty.SmsLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SmsLogDao {

    int insert(SmsLogModel smsLogModel);

    int updateStatus(@Param("status") Integer status,@Param("id") Long id);

    /**
     * 查询合集
     * @param templateId
     * @param mobile
     * @param beginTime
     * @param endTime
     * @param ip
     * @param page
     * @param size
     * @return
     */
    List<SmsLogModel> search(@Param("templateId") String templateId,
            @Param("mobile") String mobile,
            @Param("status") Integer status,
            @Param("beginTime") Long beginTime,
            @Param("endTime") Long endTime,
            @Param("ip") Long ip,
            @Param("order") Map<String,String> order,
            @Param("page") Integer page,
            @Param("size") Integer size);

    Integer getCount(@Param("templateId") String templateId,
                     @Param("mobile") String mobile,
                     @Param("status") Integer status,
                     @Param("beginTime") Integer beginTime,
                     @Param("endTime") Integer endTime,
                     @Param("ip") Long ip);
}
