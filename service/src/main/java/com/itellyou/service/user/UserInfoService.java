package com.itellyou.service.user;

import com.itellyou.model.user.UserInfoModel;

public interface UserInfoService {

    int updateByUserId(UserInfoModel infoModel);

    int updateStarCount(Long id, Integer step);

    int updateFollowerCount(Long id,Integer step);

    int updateQuestionCount(Long id,Integer step);

    int updateAnswerCount(Long id,Integer step);

    int updateArticleCount(Long id,Integer step);

    int updateColumnCount(Long id,Integer step);

    int updateCollectionCount(Long id,Integer step);
}
