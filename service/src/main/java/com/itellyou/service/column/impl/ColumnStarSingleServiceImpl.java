package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnStarDao;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.service.column.ColumnStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "column_star")
@Service
public class ColumnStarSingleServiceImpl implements ColumnStarSingleService {

    private final ColumnStarDao starDao;

    public ColumnStarSingleServiceImpl(ColumnStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#columnId).concat('-').concat(#userId)",unless = "#result == null")
    public ColumnStarModel find(Long columnId, Long userId) {
        List<ColumnStarModel> starModels = starDao.search(columnId != null ? new HashSet<Long>(){{add(columnId);}} : null,userId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<ColumnStarModel> search(HashSet<Long> columnIds, Long userId) {
        return RedisUtils.fetchByCache("column_star_" + userId,ColumnStarModel.class,columnIds,(HashSet<Long> fetchIds) ->
                starDao.search(fetchIds,userId,null,null,null,null,null,null)
                ,(ColumnStarModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
