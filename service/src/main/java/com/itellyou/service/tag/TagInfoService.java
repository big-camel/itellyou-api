package com.itellyou.service.tag;

import com.itellyou.model.tag.TagInfoModel;

import java.util.Collection;

public interface TagInfoService {

    int insert(TagInfoModel tagInfoModel);

    int updateStarCountById(Long id,Integer step);
    int updateStarCountById(Collection<Long> ids,Integer step);
    int updateArticleCountById(Long id,Integer step);
    int updateArticleCountById(Collection<Long> ids, Integer step);
    int updateQuestionCountById(Long id,Integer step);
    int updateQuestionCountById(Collection<Long> ids,Integer step);

    int updateGroupByGroupId(Long nextGroupId,Long prevGroupId);

    int updateById(Long id,String name,Long groupId,Boolean isDisabled);

    int updateInfo( Long id,
                    String description,
                    Long time,
                    Long ip,
                    Long userId);
}
