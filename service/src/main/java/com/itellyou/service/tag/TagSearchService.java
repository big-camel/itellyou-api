package com.itellyou.service.tag;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface TagSearchService {

    int exists(List<Long> ids);

    int exists(Long... ids);

    List<TagDetailModel> search(HashSet<Long> ids, String name, String mode, Long groupId, Long userId,
                                Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip,
                                Integer minStar, Integer maxStar,
                                Integer minQuestion, Integer maxQuestion,
                                Integer minArticle, Integer maxArticle,
                                Long beginTime, Long endTime,
                                Map<String, String> order,
                                Integer offset,
                                Integer limit);

    int count(HashSet<Long> ids, String name, String mode, Long groupId, Long userId, Boolean isDisabled, Boolean isPublished, Long ip,
              Integer minStar, Integer maxStar,
              Integer minQuestion, Integer maxQuestion,
              Integer minArticle, Integer maxArticle,
              Long beginTime, Long endTime);

    List<TagDetailModel> search(String name, String mode, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit);

    List<TagDetailModel> search(String name, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit);

    List<TagDetailModel> search(String name, String mode, Integer offset, Integer limit);

    List<TagDetailModel> search(String name, Integer offset, Integer limit);

    PageModel<TagDetailModel> page(String name, String mode, Long groupId, Long userId,
                                   Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip,
                                   Integer minStar, Integer maxStar,
                                   Integer minQuestion, Integer maxQuestion,
                                   Integer minArticle, Integer maxArticle,
                                   Long beginTime, Long endTime,
                                   Map<String, String> order,
                                   Integer offset,
                                   Integer limit);

    TagInfoModel findById(Long id);

    TagInfoModel findByName(String name);

    TagDetailModel getDetail(Long id, Long userId, String mode, Long searchUserId, Boolean hasContent);

    TagDetailModel getDetail(Long id, String mode, Long userId);

    TagDetailModel getDetail(Long id);
}
