package com.itellyou.service.sys;

import com.itellyou.model.sys.SysAdSlotModel;

public interface SysAdSlotService {

    int insert(SysAdSlotModel model);

    int updateById(SysAdSlotModel model);

    int deleteById(long id);

    int deleteByAdId(long adId);
}
