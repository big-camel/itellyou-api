package com.itellyou.dao.user;

import com.itellyou.model.user.UserThirdAccountAction;
import com.itellyou.model.user.UserThirdAccountModel;
import com.itellyou.model.user.UserThirdAccountType;
import com.itellyou.model.user.UserThirdLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserThirdLogDao {
    /**
     * 新增第三方账户操作日志
     * @param model UserThirdLog
     * @return 受影响行数
     */
    int insert(UserThirdLogModel model);

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
    UserThirdLogModel find(String id);

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
    List<UserThirdLogModel> search(@Param("userId") Long userId,
                                       @Param("type") UserThirdAccountType type,
                                       @Param("action") UserThirdAccountAction action,
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
              @Param("type") UserThirdAccountType type,
              @Param("action") UserThirdAccountAction action,
              @Param("isVerify") Boolean isVerify,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
