package com.itellyou.model.thirdparty;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
/**
 * 用户第三方账号绑定日志类
 */
public class ThirdLogModel {
    @JSONField(label = "base")
    private String id;//主键
    @JSONField(label = "base",serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private ThirdAccountAction action;//第三方账号操作
    @JSONField(label = "base",serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private ThirdAccountType type;//第三方账号类型
    @JSONField(label = "base")
    private boolean isVerify;//是否已验证
    @JSONField(label = "base")
    private String redirectUri;//验证成功后，回调地址
    private Long createdUserId=0l;//创建者
    @JSONField(label = "base")
    private LocalDateTime createdTime;//创建时间
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;//创建ip
}
