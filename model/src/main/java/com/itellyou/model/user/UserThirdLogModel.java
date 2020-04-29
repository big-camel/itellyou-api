package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
/**
 * 用户第三方账号绑定日志类
 */
public class UserThirdLogModel {
    @JSONField(label = "base")
    private String id;//主键
    @JSONField(label = "base")
    private UserThirdAccountAction action;//第三方账号操作
    @JSONField(label = "base")
    private UserThirdAccountType type;//第三方账号类型
    @JSONField(label = "base")
    private boolean isVerify;//是否已验证
    @JSONField(label = "base")
    private String redirectUri;//验证成功后，回调地址
    private Long createdUserId=0l;//创建者
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime=0l;//创建时间
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp;//创建ip
}
