package com.itellyou.service.common;

import java.util.Map;

public interface ConfigMapService<T> {

    T find(String key);

    Map<String,T> getMap();
}
