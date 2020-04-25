package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserNotificationDetailModel extends UserNotificationModel {

    @JSONField(label = "base")
    private List<UserDetailModel> actors;
    @JSONField(label = "base")
    private Object target;
}
