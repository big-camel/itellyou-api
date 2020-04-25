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
public class UserStarDetailModel extends UserStarModel {
    @JSONField(label = "base")
    private UserDetailModel star;
    @JSONField(label = "base")
    private UserDetailModel follower;

    public UserStarDetailModel(UserStarModel model,UserDetailModel star,UserDetailModel follower){
        super(model.getUserId(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp());
        this.star = star;
        this.follower = follower;
    }

    public UserStarDetailModel(UserStarModel model)
    {
        this(model,null,null);
    }
}
