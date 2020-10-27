package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class SysIncomeRelatedDetailModel extends SysIncomeRelatedModel {

    @JSONField( label = "base")
    private SysIncomeConfigModel config;

    public SysIncomeRelatedDetailModel(SysIncomeRelatedModel relatedModel){
        super(relatedModel.getId(),relatedModel.getIncomeId(),relatedModel.getConfigId(),relatedModel.getAmount(),relatedModel.getCreatedTime(),relatedModel.getCreatedUserId(),relatedModel.getCreatedIp());
    }
}
