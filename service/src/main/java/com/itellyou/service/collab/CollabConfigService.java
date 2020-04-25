package com.itellyou.service.collab;

import com.itellyou.model.collab.CollabConfigModel;

public interface CollabConfigService {

    CollabConfigModel findByKey(String key);
}
