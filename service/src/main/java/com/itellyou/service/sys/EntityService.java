package com.itellyou.service.sys;

import com.itellyou.model.sys.EntityType;

import java.util.HashSet;
import java.util.Map;

public interface EntityService {

    Map<EntityType,Map<Long,Object>> find(Map<EntityType,HashSet<Long>> ids,Long searchId,Integer childCount);
}
