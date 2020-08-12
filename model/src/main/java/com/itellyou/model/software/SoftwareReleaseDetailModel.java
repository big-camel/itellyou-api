package com.itellyou.model.software;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JSONDefault(includes = "base")
public class SoftwareReleaseDetailModel extends SoftwareReleaseModel {
    @JSONField(label = "base")
    private List<SoftwareUpdaterDetailModel> updaters = new ArrayList<>();

    public SoftwareReleaseDetailModel(SoftwareReleaseModel model){
        super(model.getId(),model.getSoftwareId(),model.getName(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }
}
