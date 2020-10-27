package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserBankLogDetailModel extends UserBankLogModel {
    @JSONField(label = "base")
    private Object target;
    @JSONField(label = "base")
    private UserDetailModel user;

    public UserBankLogDetailModel(UserBankLogModel model){
        super(model.getId(),model.getAmount(),model.getBalance(),model.getType(),model.getAction(),model.getDataType(),model.getDataKey(),model.getRemark(),model.getCreatedTime(),model.getCreatedIp(),model.getCreatedUserId());
    }
}
