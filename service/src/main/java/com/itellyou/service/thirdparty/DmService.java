package com.itellyou.service.thirdparty;

import com.itellyou.model.thirdparty.DmLogModel;

import java.util.Map;

public interface DmService {
    DmLogModel send(String action, String email, Map<String, String> data, String ip) throws Exception;
}
