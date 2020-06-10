package com.itellyou.service.tag;

import com.itellyou.model.tag.TagInfoModel;

import java.util.List;

public interface TagSingleService {

    TagInfoModel findById(Long id);

    TagInfoModel findByName(String name);

    int exists(List<Long> ids);

    int exists(Long... ids);
}
