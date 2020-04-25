package com.itellyou.service.ali;

import com.itellyou.model.ali.DmConfigModel;

import java.util.Map;

public interface DmConfigService {

    DmConfigModel get(String type);

    Map<String,DmConfigModel> getAll();
}
