package com.itellyou.model.software;

import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareVersionDetailModel extends SoftwareVersionModel {

    private UserInfoModel author;

    private List<TagDetailModel> tags;

    public SoftwareVersionDetailModel(SoftwareVersionModel versionModel){
        super(versionModel.getId(),versionModel.getSoftwareId(),versionModel.getGroupId(),versionModel.getName(),versionModel.getLogo(),versionModel.getContent(),versionModel.getHtml(),versionModel.getDescription(),versionModel.getVersion(),versionModel.isReviewed(),versionModel.isDisabled(),versionModel.isPublished(),versionModel.getRemark(),versionModel.getSaveType(),versionModel.getCreatedTime(),versionModel.getCreatedUserId(),versionModel.getCreatedIp(),versionModel.getUpdatedTime(),versionModel.getUpdatedUserId(),versionModel.getUpdatedIp());
    }
}
