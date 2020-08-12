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
public class SoftwareUpdaterDetailModel extends SoftwareUpdaterModel {
    @JSONField(label = "base")
    private List<SoftwareFileModel> files = new ArrayList<>();

    public SoftwareUpdaterDetailModel(SoftwareUpdaterModel model){
        super(model.getId(),model.getReleaseId(),model.getName(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }
}
