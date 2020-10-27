package com.itellyou.model.statistics;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class StatisticsDetailModel extends StatisticsInfoModel {
    @JSONField(label = "base")
    private Object target;
    @JSONField(label = "base")
    private UserDetailModel user;

    public StatisticsDetailModel(StatisticsInfoModel infoModel){
        super(infoModel.getId(),infoModel.getUserId(),infoModel.getDate(),infoModel.getDataType(),infoModel.getDataKey(),infoModel.getViewCount(),infoModel.getCommentCount(),infoModel.getSupportCount(),infoModel.getOpposeCount(),infoModel.getStarCount(),infoModel.getCreatedTime(),infoModel.getCreatedUserId(),infoModel.getCreatedIp(),infoModel.getUpdatedTime(),infoModel.getUpdatedUserId(),infoModel.getUpdatedIp());
    }
}
