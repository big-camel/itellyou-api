package com.itellyou.service.tag;

import com.itellyou.model.tag.TagInfoModel;

import java.util.List;

public interface TagInfoService {

    int insert(TagInfoModel tagInfoModel);

    int updateStarCountById(Long id,Integer step);
    int updateStarCountById(List<Long> ids,Integer step);
    int updateArticleCountById(Long id,Integer step);
    int updateArticleCountById(List<Long> ids,Integer step);
    int updateQuestionCountById(Long id,Integer step);
    int updateQuestionCountById(List<Long> ids,Integer step);



    Long create(Long userId,String name, String content, String html,String icon, String description, String remark, String save_type, Long ip) throws Exception;

}
