package com.itellyou.dao.thirdparty;

import com.itellyou.model.thirdparty.ThirdAccountAction;
import com.itellyou.model.thirdparty.ThirdAccountType;
import com.itellyou.model.thirdparty.ThirdLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ThirdLogDao {
    /**
     * 新增第三方账户操作日志
     * @param model UserThirdLog
     * @return 受影响行数
     */
    int insert(ThirdLogModel model);

    /**
     * 根据id删除日志
     * @param id 主键
     * @return 受影响行数
     */
    int delete(String id);

    /**
     * 更新验证状态
     * @param isVerify
     * @param id
     * @return 受影响行数
     */
    int updateVerify(@Param("isVerify")boolean isVerify,@Param("id")String id);

    /**
     * 根据id查找编号
     * @param id
     * @return
     */
    ThirdLogModel find(String id);

    /**
     * 查询
     * @param userId
     * @param type
     * @param action,
     * @param isVerify,
     * @param beginTime
     * @param endTime
     * @param ip
     * @param order
     * @param offset
     * @param limit
     * @return
     */
    List<ThirdLogModel> search(@Param("userId") Long userId,
                               @Param("type") ThirdAccountType type,
                               @Param("action") ThirdAccountAction action,
                               @Param("isVerify") Boolean isVerify,
                               @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                               @Param("ip") Long ip,
                               @Param("order") Map<String, String> order,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    /**
     * 计算查询记录条数
     * @param userId
     * @param type
     * @param action,
     * @param isVerify,
     * @param beginTime
     * @param endTime
     * @param ip
     * @return
     */
    int count(@Param("userId") Long userId,
              @Param("type") ThirdAccountType type,
              @Param("action") ThirdAccountAction action,
              @Param("isVerify") Boolean isVerify,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
