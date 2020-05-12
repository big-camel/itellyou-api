package com.itellyou.service.column;

import com.itellyou.model.column.ColumnInfoModel;

public interface ColumnInfoService {

    int insert(ColumnInfoModel infoModel,Long... tags) throws Exception;

    int insertTag( Long columnId,Long... tags);

    int deleteTag(Long columnId);

    int updateArticles(Long id,Integer value);

    int updateStars(Long id,Integer value);

    int update(ColumnInfoModel model);

    int update(ColumnInfoModel model,String path) throws Exception;

    int updateDeleted(boolean deleted, Long id,Long userId,Long ip);
}
