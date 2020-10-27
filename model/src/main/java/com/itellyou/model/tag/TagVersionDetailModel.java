package com.itellyou.model.tag;

import com.itellyou.model.user.UserInfoModel;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TagVersionDetailModel extends TagVersionModel {

    private UserInfoModel author;

    public TagVersionDetailModel(TagVersionModel versionModel){
        super(versionModel.getId(),versionModel.getTagId(),versionModel.getIcon(),versionModel.getContent(),versionModel.getHtml(),versionModel.getDescription(),versionModel.getVersion(),versionModel.isReviewed(),versionModel.isDisabled(),versionModel.isPublished(),versionModel.getRemark(),versionModel.getSaveType(),versionModel.getCreatedTime(),versionModel.getCreatedUserId(),versionModel.getCreatedIp(),versionModel.getUpdatedTime(),versionModel.getUpdatedUserId(),versionModel.getUpdatedIp());
    }
}
