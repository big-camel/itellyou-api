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
public class UserDraftDetailModel extends UserDraftModel {
    @JSONField(label = "base")
    private UserDetailModel author;
    @JSONField(label = "base")
    private Object target;
}
