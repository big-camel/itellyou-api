package com.itellyou.model.article;

import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVersionDetailModel extends ArticleVersionModel {

    private UserInfoModel author;

    private List<TagDetailModel> tags;

    public ArticleVersionDetailModel(ArticleVersionModel versionModel){
        super(versionModel.getId(),versionModel.getArticleId(),versionModel.getColumnId(),versionModel.getSourceType(),versionModel.getSourceData(),versionModel.getTitle(),versionModel.getContent(),versionModel.getHtml(),versionModel.getDescription(),versionModel.getVersion(),versionModel.isReviewed(),versionModel.isDisabled(),versionModel.isPublished(),versionModel.getRemark(),versionModel.getSaveType(),versionModel.getCreatedTime(),versionModel.getCreatedUserId(),versionModel.getCreatedIp(),versionModel.getUpdatedTime(),versionModel.getUpdatedUserId(),versionModel.getUpdatedIp());
    }
}
