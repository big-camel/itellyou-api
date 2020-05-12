package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.util.annotation.JSONDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class OperationalBaseModel {
    @JSONField(label = "base")
    private EntityAction action;
    @JSONField(label = "base")
    private EntityType type;
    private Long targetId;
}
