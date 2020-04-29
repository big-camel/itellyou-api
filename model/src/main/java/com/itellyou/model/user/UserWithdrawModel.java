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
@JSONDefault(includes = "base")
public class UserWithdrawModel {
    @JSONField(label = "base")
    private String id;
    @JSONField(label = "base")
    private double amount;
    @JSONField(label = "base")
    private String subject;
    @JSONField(label = "base")
    private UserPaymentStatus status;
    @JSONField(label = "base")
    private double commissionCharge;
    @JSONField(label = "base",serializeUsing = TimestampSerializer.class)
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long updatedTime = 0l;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long updatedIp = 0l;
}
