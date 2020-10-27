package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.annotation.Privacy;
import com.itellyou.util.serialize.*;
import com.itellyou.util.validation.Mobile;
import com.itellyou.util.validation.Name;
import com.itellyou.util.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class UserInfoModel implements Serializable , CacheEntity {

    @Override
    public Long cacheKey() {
        return this.id;
    }

    public interface RegisterAction {}

    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base",name = "login")
    private String loginName;

    @NotBlank(groups = RegisterAction.class)
    @Password(groups = RegisterAction.class)
    @JSONField(serialize = false)
    private String loginPassword;

    @JSONField(serialize = false)
    private String payPassword;

    @NotBlank(groups = RegisterAction.class)
    @Name(groups = RegisterAction.class)
    @JSONField(label = "base")
    private String name;

    @JSONField(label = "info")
    private Integer gender=0;

    @JSONField(label = "info")
    private Long birthday=0l;

    @NotBlank(groups = RegisterAction.class)
    @Mobile(groups = RegisterAction.class)
    @Privacy
    @JSONField(label = "account")
    private String mobile;

    @JSONField(label = "mobile.status")
    private boolean mobileStatus;

    @NotBlank
    @Email
    @Privacy
    @JSONField(label = "account")
    private String email;

    @JSONField(label = "email.status")
    private boolean emailStatus;

    @JSONField(label = "base")
    private String description;

    @JSONField(label = "info")
    private String introduction;

    @JSONField(label = "info")
    private String profession;

    @JSONField(label = "info")
    private String address;

    @JSONField(label = "base")
    private String avatar;

    private boolean isDisabled;

    @JSONField(label = "base")
    private Integer starCount=0;
    @JSONField(label = "base")
    private Integer followerCount=0;
    @JSONField(label = "base")
    private Integer questionCount=0;
    @JSONField(label = "base")
    private Integer answerCount=0;
    @JSONField(label = "base")
    private Integer articleCount=0;
    @JSONField(label = "base")
    private Integer columnCount=0;
    @JSONField(label = "base")
    private Integer collectionCount=0;

    @JSONField(label = "time")
    private LocalDateTime createdTime;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp=0l;

    private LocalDateTime updatedTime;
    private Long updatedUserId=0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp=0l;

    public UserInfoModel(Long id,String loginName,String name,String loginPassword,String payPassword,Integer gender,Long birthday,String mobile,boolean mobileStatus,String email,boolean emailStatus,
                         String description,String introduction,String profession,String address,String avatar,boolean isDisabled,Long updatedUserId,LocalDateTime updatedTime,Long updatedIp){
        this.id = id;
        this.loginName = loginName;
        this.name = name;
        this.loginPassword = loginPassword;
        this.payPassword = payPassword;
        this.gender = gender;
        this.birthday = birthday;
        this.mobile = mobile;
        this.mobileStatus = mobileStatus;
        this.email = email;
        this.emailStatus = emailStatus;
        this.description = description;
        this.introduction = introduction;
        this.profession = profession;
        this.address = address;
        this.avatar = avatar;
        this.isDisabled = isDisabled;
        this.updatedUserId = updatedUserId;
        this.updatedTime = updatedTime;
        this.updatedIp = updatedIp;
    }
}
