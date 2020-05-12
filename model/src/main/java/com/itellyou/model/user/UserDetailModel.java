package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.CacheEntity;
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
