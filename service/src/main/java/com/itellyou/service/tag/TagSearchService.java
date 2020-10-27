package com.itellyou.service.tag;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagSearchService {

    List<TagDetailModel> search(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Long userId,
                                Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip,
                                Integer minStar, Integer maxStar,
                                Integer minQuestion, Integer maxQuestion,
                                Integer minArticle, Integer maxArticle,
                                Long beginTime, Long endTime,
                                Map<String, String> order,
                                Integer offset,
                                Integer limit);

    int count(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Long userId, Boolean isDisabled, Boolean isPublished, Long ip,
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

    List<TagInfoModel> searchChild(Collection<Long> ids,
                                   String name,
                                   String mode,
                                   Collection<Long> groupIds,
                                   Integer childCount,
                                   Long userId,
                                   Boolean isDisabled,
                                   Boolean isPublished,
                                   Long ip,
                                   Integer minStar, Integer maxStar,
                                   Integer minQuestion, Integer maxQuestion,
                                   Integer minArticle, Integer maxArticle,
                                   Long beginTime, Long endTime,
                                   Map<String,String> order);

    TagDetailModel getDetail(Long id, Long userId, String mode, Long searchUserId, Boolean hasContent);

    TagDetailModel getDetail(Long id, String mode, Long userId);

    TagDetailModel getDetail(Long id);
}
