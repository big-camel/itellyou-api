package com.itellyou.service.question;

import com.itellyou.model.question.*;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.view.ViewInfoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionInfoService {
    int insert(QuestionInfoModel questionInfoModel);

    int updateView(Long userId, Long id, Long ip, String os, String browser);

    int updateAnswers(Long id,Integer value);
    int updateAdopt(Boolean isAdopted,Long adoptionId,Long id);

    int updateComments(Long id, Integer value);

    int updateStarCountById(Long id,Integer step);

    Long create(Long userId,String title, String content, String html, String description,RewardType rewardType,Double rewardValue, Double rewardAdd, List<TagInfoModel> tags,String remark, String save_type, Long ip) throws Exception;

    int updateDeleted(boolean deleted,Long id,Long userId);

    int updateMetas(Long id, String cover);
}
