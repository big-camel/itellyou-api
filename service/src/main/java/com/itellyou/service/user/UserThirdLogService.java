package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserThirdAccountAction;
import com.itellyou.model.user.UserThirdAccountModel;
import com.itellyou.model.user.UserThirdAccountType;
import com.itellyou.model.user.UserThirdLogModel;

import java.util.List;
import java.util.Map;

public interface UserThirdLogService {
    /**
     * 新增第三方账户操作日志
     * @param model UserThirdLog
     * @return 受影响行数
     */
    int insert(UserThirdLogModel model) throws Exception;

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
    int updateVerify(boolean isVerify,String id);

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
    List<UserThirdLogModel> search(Long userId, UserThirdAccountType type,
                                       UserThirdAccountAction action,
                                       Boolean isVerify,
                                       Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order,
                                       Integer offset,
                                       Integer limit);

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
    int count(Long userId, UserThirdAccountType type,
              UserThirdAccountAction action,
              Boolean isVerify,
              Long beginTime, Long endTime,
              Long ip);


    /**
     * 分页查询
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
    PageModel<UserThirdLogModel> page(Long userId, UserThirdAccountType type,
                                          UserThirdAccountAction action,
                                          Boolean isVerify,
                                          Long beginTime, Long endTime,
                                          Long ip,
                                          Map<String, String> order,
                                          Integer offset,
                                          Integer limit);
}
