package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class SysAdDetailModel extends SysAdModel {
    @JSONField(label = "base")
    private List<SysAdSlotModel> slots;

    public SysAdDetailModel(SysAdModel model){
        super(model.getId(),model.getName(),model.getType(),model.getDataId(),model.getEnabledForeign(),model.getEnabledCn(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }
}
