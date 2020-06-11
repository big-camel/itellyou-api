package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserDetailModel extends UserInfoModel implements Serializable, CacheEntity {

    public UserDetailModel(UserInfoModel infoModel){
        super(infoModel.getId(),infoModel.getLoginName(),infoModel.getLoginPassword(),infoModel.getPayPassword(),infoModel.getName(),infoModel.getGender(),infoModel.getBirthday(),infoModel.getMobile(),infoModel.isMobileStatus(),infoModel.getEmail(),infoModel.isEmailStatus(),
                infoModel.getDescription(),infoModel.getIntroduction(),infoModel.getProfession(),infoModel.getAddress(),infoModel.getAvatar(),infoModel.isDisabled(),
                infoModel.getStarCount(),infoModel.getFollowerCount(),infoModel.getQuestionCount(),infoModel.getAnswerCount(),infoModel.getArticleCount(),infoModel.getColumnCount(),infoModel.getCollectionCount(),infoModel.getCreatedTime(),infoModel.getCreatedUserId(),infoModel.getCreatedIp(),infoModel.getUpdatedTime(),infoModel.getUpdatedUserId(),infoModel.getUpdatedIp());
    }

    @JSONField(label = "bank")
    private UserBankModel bank;
    @JSONField(label = "base")
    private String path="";
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "rank")
    private UserRankModel rank;
    @Override
    public String cacheKey() {
        return String.valueOf(this.getId());
    }
}
