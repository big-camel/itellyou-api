package com.itellyou.service.common;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.sys.EntityType;

import java.util.Collection;

public interface IndexManagerService {

    void put(IndexQueueModel model);

    void put(EntityType type, Collection<Long> ids);
}
