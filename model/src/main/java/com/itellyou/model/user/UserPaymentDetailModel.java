package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserPaymentDetailModel extends UserPaymentModel {
    @JSONField(label = "base")
    private UserDetailModel user;

    public UserPaymentDetailModel(UserPaymentModel paymentModel){
        super(paymentModel.getId(),paymentModel.getAmount(),paymentModel.getType(),paymentModel.getSubject(),paymentModel.getStatus(),paymentModel.getCreatedTime(),paymentModel.getCreatedUserId(),paymentModel.getCreatedIp(),paymentModel.getUpdatedTime(),paymentModel.getUpdatedUserId(),paymentModel.getUpdatedIp());
    }
}
