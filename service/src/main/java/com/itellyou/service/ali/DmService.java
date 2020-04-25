package com.itellyou.service.ali;

import com.itellyou.model.ali.DmLogModel;

import java.util.Map;

public interface DmService {
    DmLogModel send(String action, String email, Map<String, String> data, String ip) throws Exception;
}
