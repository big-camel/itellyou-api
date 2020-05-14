package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class OperationalBaseModel {
    @JSONField(label = "base",serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityAction action;
    @JSONField(label = "base",serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType type;
    private Long targetId;
}
