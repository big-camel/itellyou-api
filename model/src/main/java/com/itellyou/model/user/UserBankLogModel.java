package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import com.itellyou.util.serialize.TimestampDeserializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault( includes = "base")
public class UserBankLogModel {
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
    @JSONField(label = "base",serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime;

    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    @JSONField(serialize = false)
    private Long createdUserId;

    @JSONField(label = "base")
    private UserInfoModel user;

    public UserBankLogModel(Double amount,Double balance,UserBankType type,EntityAction action,EntityType dataType,String dataKey,String remark,Long createdTime,Long createdIp,Long createdUserId){
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
}
