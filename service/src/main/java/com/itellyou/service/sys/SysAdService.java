package com.itellyou.service.sys;

import com.itellyou.model.sys.SysAdModel;

public interface SysAdService {

    int insert(SysAdModel model);

    int updateById(SysAdModel model);

    int updateEnabledForeignAll(boolean enabled);

    int updateEnabledCnAll(boolean enabled);

    int deleteById(long id);
}
