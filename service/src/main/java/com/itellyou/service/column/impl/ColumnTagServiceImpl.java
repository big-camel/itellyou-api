package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnTagDao;
import com.itellyou.model.column.ColumnTagModel;
import com.itellyou.service.column.ColumnTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "column_tag")
public class ColumnTagServiceImpl implements ColumnTagService {

    private final ColumnTagDao columnTagDao;

    public ColumnTagServiceImpl(ColumnTagDao columnTagDao) {
        this.columnTagDao = columnTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.columnId") , @CacheEvict(value = "tag_column" , allEntries = true)})
    public int add(ColumnTagModel model) {
        return columnTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#columnId") , @CacheEvict(value = "tag_column" , allEntries = true)})
    public int addAll(Long columnId, HashSet<Long> tagIds) {
        return columnTagDao.addAll(columnId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#columnId") , @CacheEvict(value = "tag_column" , allEntries = true)})
    public int clear(Long columnId) {
        return columnTagDao.clear(columnId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#columnId") , @CacheEvict(value = "tag_column" , allEntries = true)})
    public int remove(Long columnId, Long tagId) {
        return columnTagDao.remove(columnId,tagId);
    }

    @Override
    public Map<Long, List<ColumnTagModel>> searchTags(HashSet<Long> columnIds) {
        List<ColumnTagModel> models = columnTagDao.searchTags(columnIds);
        Map<Long, List<ColumnTagModel>> map = new LinkedHashMap<>();
        for (ColumnTagModel model : models){
            if(!map.containsKey(model.getColumnId())){
                map.put(model.getColumnId(),new LinkedList<>());
            }
            map.get(model.getColumnId()).add(model);
        }
        return map;
    }

    @Override
    public HashSet<Long> searchTagId(HashSet<Long> columnIds) {
        return columnTagDao.searchTagId(columnIds);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public HashSet<Long> searchTagId(Long columnId) {
        return columnTagDao.searchTagId(columnId != null ? new HashSet<Long>(){{ add(columnId);}} : null);
    }

    @Override
    @Cacheable(value = "tag_column",key = "#tagId",unless = "#result == null")
    public HashSet<Long> searchColumnId(Long tagId) {
        return searchColumnId(tagId != null ? new HashSet<Long>(){{ add(tagId);}} : null);
    }

    @Override
    @Cacheable(value = "tag_column",key = "#methodName",unless = "#result == null")
    public HashSet<Long> searchColumnId(HashSet<Long> tagId) {
        return columnTagDao.searchColumnId(tagId);
    }
}
