package com.itellyou.service.ali;

import com.itellyou.model.ali.DmTemplateModel;

import java.util.Map;

public interface DmTemplateService {

    DmTemplateModel findById(String id);

    Map<String,DmTemplateModel> getAll();
}
