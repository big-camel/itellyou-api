package com.itellyou.dao.user;

import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserInfoDao {
    /**
     * 根据tiem有效期的token获取用户基础信息
     * @param token 登录后的Token
     * @param time 有效期的时间
     * @return
     */
    UserInfoModel findByToken(@Param("token") String token, @Param("time") Long time);

    UserInfoModel findByName(String name);

    UserInfoModel findByLoginName(String loginName);

    UserInfoModel findByMobile(@Param("mobile") String mobile,@Param("status") Integer mobileStatus);

    UserInfoModel findByEmail(@Param("email") String email,@Param("status") Integer emailStatus);

    UserInfoModel findById(Long id);

    List<UserDetailModel> search(@Param("ids") HashSet<Long> ids,
                                @Param("searchUserId") Long searchUserId,
                                @Param("loginName") String loginName, @Param("name") String name,
                                 @Param("mobile") String mobile, @Param("email") String email,
                                @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                @Param("ip") Long ip,
                                @Param("order") Map<String, String> order,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    int count(@Param("ids") HashSet<Long> ids,
              @Param("loginName") String loginName, @Param("name") String name,
              @Param("mobile") String mobile, @Param("email") String email,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    int createUser(UserInfoModel userInfoModel);

    int updateByUserId(UserInfoModel infoModel);

    int updateStarCount(@Param("id") Long id,@Param("step") Integer step);

    int updateFollowerCount(@Param("id") Long id,@Param("step") Integer step);

    int updateQuestionCount(@Param("id") Long id,@Param("step") Integer step);

    int updateAnswerCount(@Param("id") Long id,@Param("step") Integer step);

    int updateArticleCount(@Param("id") Long id,@Param("step") Integer step);

    int updateColumnCount(@Param("id") Long id,@Param("step") Integer step);

    int updateCollectionCount(@Param("id") Long id,@Param("step") Integer step);
}
