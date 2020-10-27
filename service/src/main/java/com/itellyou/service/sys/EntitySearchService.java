package com.itellyou.service.sys;

import com.itellyou.util.CacheEntity;

import java.util.List;
import java.util.Map;

public interface EntitySearchService<T extends CacheEntity > {
    List<T> search(Map<String,Object> args);
}
