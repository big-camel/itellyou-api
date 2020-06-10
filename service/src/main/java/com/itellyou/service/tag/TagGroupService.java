package com.itellyou.service.tag;

import com.itellyou.model.tag.TagGroupModel;

public interface TagGroupService {
    int insert(TagGroupModel groupModel);

    int updateTagCountById(Long id,Integer step);

    int updateNameById(Long id, String name);

    int deleteById(Long id);
}
