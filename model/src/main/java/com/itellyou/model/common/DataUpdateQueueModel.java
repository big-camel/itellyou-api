package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataUpdateQueueModel<T extends DataUpdateStepModel>  {
    private Long userId;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType dataType;
    private Long date;
    private T data;
}
