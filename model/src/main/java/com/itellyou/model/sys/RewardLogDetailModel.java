package com.itellyou.model.sys;

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
public class RewardLogDetailModel extends RewardLogModel{
    @JSONField(label = "base")
    private UserDetailModel targetUser;
    @JSONField(label = "base")
    private Object targetData;
    @JSONField(label = "base")
    private UserDetailModel createdUser;

    public RewardLogDetailModel(RewardLogModel model){
        super(model.getId(),model.getBankType(),model.getDataType(),model.getDataKey(),model.getAmount(),model.getUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getDataKey());
    }
}
