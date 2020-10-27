package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnTagDao;
import com.itellyou.model.column.ColumnTagModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.column.ColumnTagService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = CacheKeys.COLUMN_TAG_KEY)
public class ColumnTagServiceImpl implements ColumnTagService {

    private final ColumnTagDao columnTagDao;

    public ColumnTagServiceImpl(ColumnTagDao columnTagDao) {
        this.columnTagDao = columnTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.columnId") , @CacheEvict(value = CacheKeys.TAG_COLUMN_KEY , key = "#model.tagId")})
    public int add(ColumnTagModel model) {
        return columnTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#columnId")})
    public int addAll(Long columnId, Collection<Long> tagIds) {
        tagIds.forEach((Long id) -> RedisUtils.remove(CacheKeys.TAG_COLUMN_KEY,id));
        return columnTagDao.addAll(columnId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#columnId")})
    public int clear(Long columnId) {
        Map<Long, List<ColumnTagModel>> tags = searchTags(new HashSet<Long>(){{add(columnId);}});
        tags.values().forEach((List<ColumnTagModel> models) ->
                models.forEach((ColumnTagModel model) -> RedisUtils.remove(CacheKeys.TAG_COLUMN_KEY,model.getTagId()))
        );
        return columnTagDao.clear(columnId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#columnId") , @CacheEvict(value = CacheKeys.TAG_COLUMN_KEY , key = "#tagId")})
    public int remove(Long columnId, Long tagId) {
        return columnTagDao.remove(columnId,tagId);
    }

    @Override
    public Map<Long, List<ColumnTagModel>> searchTags(Collection<Long> columnIds) {
        return RedisUtils.fetch(CacheKeys.COLUMN_TAG_KEY,columnIds,
                fetchIds -> columnTagDao.searchTags(fetchIds),
                ColumnTagModel::getColumnId);
    }

    @Override
    public Map<Long, List<ColumnTagModel>> searchColumns(Collection<Long> tagIds) {
        return RedisUtils.fetch(CacheKeys.TAG_COLUMN_KEY,tagIds,
                fetchIds -> columnTagDao.searchColumns(fetchIds),
                ColumnTagModel::getTagId);
    }

    @Override
    public Collection<Long> searchColumnIds(Collection<Long> tagIds) {
        Map<Long, List<ColumnTagModel>> list = searchColumns(tagIds);
        Collection<Long> ids = new LinkedHashSet<>();
        for (Long id : list.keySet()){
            ids.addAll(list.get(id).stream().map(ColumnTagModel::getColumnId).collect(Collectors.toSet()));
        }
        return ids;
    }
}
