package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.DateUtils;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ViewInfoModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String title;
    @JSONField(label = "base")
    private String os;
    @JSONField(label = "base")
    private String browser;
    @JSONField(label = "base", serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType dataType;
    @JSONField(label = "base")
    private Long dataKey;
    @JSONField(serialize = false)
    private Long createdUserId;
    private UserInfoModel user;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class,label = "base")
    private Long createdTime;
    @JSONField(serialize = false,serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    @JSONField(serialize = false)
    private Long updatedUserId;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class,label = "base")
    private Long updatedTime;
    @JSONField(serialize = false,serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp;

    public ViewInfoModel(String title,String os,String browser,EntityType dataType,Long dataKey,Long userId,Long ip){
        this.title = title;
        this.os = os;
        this.browser = browser;
        this.dataKey = dataKey;
        this.dataType = dataType;
        this.createdUserId = userId;
        this.updatedUserId = userId;
        this.createdIp = ip;
        this.updatedIp = ip;
        this.createdTime = DateUtils.getTimestamp();
        this.updatedTime = DateUtils.getTimestamp();
    }
}
