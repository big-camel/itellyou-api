package com.itellyou.model.thirdparty;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itellyou.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsLogModel implements Serializable {
    private Long id;
    private String mobile;
    private String templateId;
    private String data;
    private Integer status;
    private Long createdTime;
    private Long createdIp;
}
