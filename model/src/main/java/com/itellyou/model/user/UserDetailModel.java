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
public class UserDetailModel extends UserInfoModel {

    @JSONField(label = "bank")
    private UserBankModel bank;
    @JSONField(label = "base")
    private String path="";
    @JSONField(label = "base")
    private boolean useStar;
}
