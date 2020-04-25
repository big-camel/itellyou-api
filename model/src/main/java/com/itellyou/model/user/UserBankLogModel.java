package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
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
    private UserBankType type;
    @JSONField(label = "base")
    private Double balance;
    @JSONField(label = "base")
    private UserBankLogType dataType;
    @JSONField(label = "base")
    private String dataKey;
    @JSONField(label = "base")
    private String remark;
    @JSONField(label = "base",serializeUsing = TimestampSerializer.class)
    private Long createdTime;

    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp;

    @JSONField(serialize = false)
    private Long createdUserId;

    @JSONField(label = "base")
    private UserInfoModel user;

    public UserBankLogModel(Double amount,UserBankType type,Double balance,UserBankLogType dataType,String dataKey,String remark,Long createdTime,Long createdIp,Long createdUserId){
        this.amount = amount;
        this.type = type;
        this.balance = balance;
        this.dataType = dataType;
        this.dataKey = dataKey;
        this.remark = remark;
        this.createdTime = createdTime;
        this.createdIp = createdIp;
        this.createdUserId = createdUserId;
    }
}
