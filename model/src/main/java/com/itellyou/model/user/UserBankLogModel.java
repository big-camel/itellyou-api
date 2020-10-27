package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserBankLogModel implements CacheEntity<Long> {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private Double amount;
    @JSONField(label = "base")
    private Double balance;
    @JSONField(label = "base")
    private UserBankType type;
    @JSONField(label = "base")
    private EntityAction action;
    @JSONField(label = "base")
    private EntityType dataType;
    @JSONField(label = "base")
    private String dataKey;
    @JSONField(label = "base")
    private String remark;
    @JSONField(label = "base")
    private LocalDateTime createdTime;

    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    private Long createdUserId;

    public UserBankLogModel(Double amount,Double balance,UserBankType type,EntityAction action,EntityType dataType,String dataKey,String remark,LocalDateTime createdTime,Long createdIp,Long createdUserId){
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.action = action;
        this.dataType = dataType;
        this.dataKey = dataKey;
        this.remark = remark;
        this.createdTime = createdTime;
        this.createdIp = createdIp;
        this.createdUserId = createdUserId;
    }

    @Override
    public Long cacheKey() {
        return id;
    }
}
