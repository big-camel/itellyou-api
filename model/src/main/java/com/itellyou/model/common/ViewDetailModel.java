package com.itellyou.model.common;

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
public class ViewDetailModel extends ViewInfoModel {
    @JSONField(label = "base")
    private Object target;
    @JSONField(label = "base")
    private UserDetailModel user;

    public ViewDetailModel(ViewInfoModel model){
        super(model.getId(),model.getTitle(),model.getOs(),model.getBrowser(),model.getDataType(),model.getDataKey(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp(),model.getUpdatedUserId(),model.getUpdatedTime(),model.getUpdatedIp());
    }
}
