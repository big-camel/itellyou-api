package com.itellyou.model.article;

import com.itellyou.model.user.UserDetailModel;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTotalDetailModel extends ArticleTotalModel {
    private UserDetailModel user;

    public ArticleTotalDetailModel(ArticleTotalModel totalModel){
        super(totalModel.getUserId(),totalModel.getTotalCount(),totalModel.getViewCount(),totalModel.getSupportCount(),totalModel.getOpposeCount(),totalModel.getStarCount(),totalModel.getCommentCount());
    }
}
