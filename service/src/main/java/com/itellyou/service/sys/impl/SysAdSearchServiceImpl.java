package com.itellyou.service.sys.impl;

import com.itellyou.model.sys.AdType;
import com.itellyou.model.sys.SysAdDetailModel;
import com.itellyou.model.sys.SysAdModel;
import com.itellyou.model.sys.SysAdSlotModel;
import com.itellyou.service.sys.SysAdSearchService;
import com.itellyou.service.sys.SysAdSingleService;
import com.itellyou.service.sys.SysAdSlotSingleService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysAdSearchServiceImpl implements SysAdSearchService {

    private final SysAdSingleService singleService;
    private final SysAdSlotSingleService slotSingleService;

    public SysAdSearchServiceImpl(SysAdSingleService singleService, SysAdSlotSingleService slotSingleService) {
        this.singleService = singleService;
        this.slotSingleService = slotSingleService;
    }

    @Override
    public List<SysAdDetailModel> search(Collection<Long> ids, AdType type, String name, Boolean enabledForeign, Boolean enabledCn, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysAdModel> adModels = singleService.search(ids,type,name,enabledForeign,enabledCn,userId,beginTime,endTime,ip,order,offset,limit);
        List<SysAdDetailModel> detailModels = new ArrayList<>();
        if(adModels.size() == 0) return detailModels;
        Collection<Long> fetchIds = adModels.stream().map(SysAdModel::getId).collect(Collectors.toSet());
        List<SysAdSlotModel> slotModels = slotSingleService.search(null,null,fetchIds,null,null,null,null,null,null,null);
        for (SysAdModel adModel : adModels){
            SysAdDetailModel detailModel = new SysAdDetailModel(adModel);
            List<SysAdSlotModel> models = slotModels.stream().filter(model -> model.getAdId().equals(adModel.getId())).collect(Collectors.toList());
            detailModel.setSlots(models);
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public SysAdDetailModel findByEnabledForeign(Boolean enabledForeign) {
        SysAdModel adModel = singleService.findByEnabledForeign(enabledForeign);
        if(adModel == null) return null;
        SysAdDetailModel detailModel = new SysAdDetailModel(adModel);
        List<SysAdSlotModel> slotModels =  slotSingleService.findByAdId(adModel.getId());
        detailModel.setSlots(slotModels);
        return detailModel;
    }

    @Override
    public SysAdDetailModel findByEnabledCn(Boolean enabledCn) {
        SysAdModel adModel = singleService.findByEnabledCn(enabledCn);
        if(adModel == null) return null;
        SysAdDetailModel detailModel = new SysAdDetailModel(adModel);
        List<SysAdSlotModel> slotModels =  slotSingleService.findByAdId(adModel.getId());
        detailModel.setSlots(slotModels);
        return detailModel;
    }
}
