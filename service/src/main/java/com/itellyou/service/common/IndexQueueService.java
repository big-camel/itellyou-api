package com.itellyou.service.common;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.sys.EntityType;

import java.util.HashSet;
import java.util.LinkedHashMap;

public interface IndexQueueService {

    void put(IndexQueueModel model);

    int size(EntityType type);

    int size();

    LinkedHashMap<EntityType, HashSet<IndexQueueModel>> reset();
}
