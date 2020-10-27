package com.itellyou.service.sys.impl;

import com.itellyou.model.sys.SysIncomeConfigModel;
import com.itellyou.model.sys.SysIncomeRelatedDetailModel;
import com.itellyou.model.sys.SysIncomeRelatedModel;
import com.itellyou.service.sys.SysIncomeConfigSingleService;
import com.itellyou.service.sys.SysIncomeRelatedSearchService;
import com.itellyou.service.sys.SysIncomeRelatedSingleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class SysIncomeRelatedSearchServiceImpl implements SysIncomeRelatedSearchService {

    private final SysIncomeRelatedSingleService singleService;
    private final SysIncomeConfigSingleService configSingleService;

    public SysIncomeRelatedSearchServiceImpl(SysIncomeRelatedSingleService singleService, SysIncomeConfigSingleService configSingleService) {
        this.singleService = singleService;
        this.configSingleService = configSingleService;
    }

    @Override
    public List<SysIncomeRelatedDetailModel> search(Collection<Long> ids,Long incomeId,Long configId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysIncomeRelatedModel> relatedModels = singleService.search(ids,incomeId,configId,userId,beginTime,endTime,ip,order,offset,limit);
        List<SysIncomeRelatedDetailModel> detailModels = new LinkedList<>();
        if(relatedModels.size() == 0) return detailModels;
        Collection<Long> configIds = new LinkedList<>();
        relatedModels.forEach(relateModel -> {
            if(!configIds.contains(relateModel.getConfigId())) configIds.add(relateModel.getConfigId());
        });
        if(configIds.size() == 0) return detailModels;
        List<SysIncomeConfigModel> configModels = configSingleService.search(configIds,null,null,null,null,null,null,null,null,null);
        relatedModels.forEach(relateModel -> {
            SysIncomeRelatedDetailModel detailModel = new SysIncomeRelatedDetailModel(relateModel);
            configModels.stream().filter(configModel -> configModel.getId().equals(relateModel.getConfigId())).findFirst().ifPresent(configModel -> {
                detailModel.setConfig(configModel);
            });
            detailModels.add(detailModel);
        });
        return detailModels;
    }
}
