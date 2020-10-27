package com.itellyou.model.statistics;

import com.itellyou.model.user.UserDetailModel;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsIncomeTotalDetailModel extends StatisticsIncomeTotalModel {
    private UserDetailModel user;

    public StatisticsIncomeTotalDetailModel(StatisticsIncomeTotalModel totalModel){
        super(totalModel.getUserId(),totalModel.getTotalAmount(),totalModel.getTipAmount(),totalModel.getRewardAmount(),totalModel.getSharingAmount(),totalModel.getSellAmount(),totalModel.getOtherAmount());
    }
}
