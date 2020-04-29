package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserThirdAccountService {
    /**
     * 新增第三方账号
     * @param model UserThirdAccountModel
     * @return 受影响行数
     */
    int insert(UserThirdAccountModel model) throws Exception;

    /**
     * 根据用户编号和第三方账户类型删除已绑定的第三方账号
     * @param userId 用户编号
     * @param type 第三方账户类型
     * @return 受影响行数
     */
    int deleteByUserIdAndType(Long userId,UserThirdAccountType type);


    Map<UserThirdAccountType , UserThirdAccountModel> searchByUserId(Long userId);

    UserThirdAccountModel searchByTypeAndKey(UserThirdAccountType type, String key);

    /**
     * 查询
     * @param userId
     * @param type
     * @param beginTime
     * @param endTime
     * @param ip
     * @param order
     * @param offset
     * @param limit
     * @return
     */
    List<UserThirdAccountModel> search(Long userId, UserThirdAccountType type,
                                     Long beginTime, Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);

    /**
     * 计算查询记录条数
     * @param userId
     * @param type
     * @param beginTime
     * @param endTime
     * @param ip
     * @return
     */
    int count(Long userId, UserThirdAccountType type,
              Long beginTime, Long endTime,
              Long ip);


    /**
     * 分页查询
     * @param userId
     * @param type
     * @param beginTime
     * @param endTime
     * @param ip
     * @param order
     * @param offset
     * @param limit
     * @return
     */
    PageModel<UserThirdAccountModel> page(Long userId, UserThirdAccountType type,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);

    String oauthAlipayURL(Long userId, UserThirdAccountAction action, String redirectUri, Long ip) throws Exception;

    String oauthGithubURL(Long userId, UserThirdAccountAction action, String redirectUri, Long ip) throws Exception;

    int bindAlipay(Long userId,String token,Long ip) throws Exception;

    int bindGithub(Long userId,String token,Long ip) throws Exception;
}
