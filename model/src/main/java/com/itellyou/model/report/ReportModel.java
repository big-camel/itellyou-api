package com.itellyou.model.report;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.DateUtils;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class ReportModel {

    private Long id;
    private ReportAction action;
    private EntityType type;
    private Integer state=0;
    private String description="";
    private Long targetId;
    private Long targetUserId;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long createdTime = DateUtils.getTimestamp();
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
}
