package com.itellyou.service.thirdparty;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.thirdparty.ThirdAccountAction;
import com.itellyou.model.thirdparty.ThirdAccountModel;
import com.itellyou.model.thirdparty.ThirdAccountType;

import java.util.List;
import java.util.Map;

public interface ThirdAccountService {
    /**
     * 新增第三方账号
     * @param model UserThirdAccountModel
     * @return 受影响行数
     */
    int insert(ThirdAccountModel model) throws Exception;

    /**
     * 根据用户编号和第三方账户类型删除已绑定的第三方账号
     * @param userId 用户编号
     * @param type 第三方账户类型
     * @return 受影响行数
     */
    int deleteByUserIdAndType(Long userId, ThirdAccountType type,Long ip);


    Map<String , ThirdAccountModel> searchByUserId(Long userId);

    ThirdAccountModel searchByTypeAndKey(ThirdAccountType type, String key);

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
    List<ThirdAccountModel> search(Long userId, ThirdAccountType type,
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
    int count(Long userId, ThirdAccountType type,
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
    PageModel<ThirdAccountModel> page(Long userId, ThirdAccountType type,
                                      Long beginTime, Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);

    String oauthAlipayURL(Long userId, ThirdAccountAction action, String redirectUri, Long ip) throws Exception;

    String oauthGithubURL(Long userId, ThirdAccountAction action, String redirectUri, Long ip) throws Exception;

    int bindAlipay(Long userId,String token,Long ip) throws Exception;

    int bindGithub(Long userId,String token,Long ip) throws Exception;
}
