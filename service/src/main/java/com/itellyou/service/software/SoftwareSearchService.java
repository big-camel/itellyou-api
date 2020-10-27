package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SoftwareSearchService {

    List<SoftwareDetailModel> search(Collection<Long> ids, String mode, Long groupId, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                    Collection<Long> tags,
                                    Integer minComment, Integer maxComment,
                                    Integer minView, Integer maxView,
                                     Integer minSupport,  Integer maxSupport,
                                     Integer minOppose,  Integer maxOppose,
                                    Long beginTime, Long endTime,
                                    Long ip,
                                    Map<String, String> order, Integer offset, Integer limit);
    int count(Collection<Long> ids, String mode, Long groupId, Long userId, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
              Collection<Long> tags,
              Integer minComment, Integer maxComment,
              Integer minView, Integer maxView,
              Integer minSupport,  Integer maxSupport,
              Integer minOppose,  Integer maxOppose,
              Long beginTime, Long endTime, Long ip);

    List<SoftwareDetailModel> search(Collection<Long> ids, String mode, Long groupId, Long userId, Long searchUserId, Boolean hasContent,
                                    Long beginTime, Long endTime,
                                    Map<String, String> order, Integer offset, Integer limit);

    List<SoftwareDetailModel> search(Long groupId, Long searchUserId,  Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                    Collection<Long> tags,
                                    Integer minComment, Integer maxComment,
                                    Integer minView, Integer maxView,
                                     Integer minSupport,  Integer maxSupport,
                                     Integer minOppose,  Integer maxOppose,
                                    Long beginTime, Long endTime,
                                    Map<String, String> order, Integer offset, Integer limit);

    List<SoftwareDetailModel> search(Long groupId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                    Collection<Long> tags,
                                    Integer minComment, Integer maxComment,
                                    Integer minView, Integer maxView,
                                     Integer minSupport,  Integer maxSupport,
                                     Integer minOppose,  Integer maxOppose,
                                    Long beginTime, Long endTime, Integer offset, Integer limit);

    int count(Long groupId, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
              Collection<Long> tags,
              Integer minComment, Integer maxComment,
              Integer minView, Integer maxView,
              Integer minSupport,  Integer maxSupport,
              Integer minOppose,  Integer maxOppose,
              Long beginTime, Long endTime);

    List<SoftwareDetailModel> search(Long groupId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                    Long beginTime, Long endTime,
                                    Map<String, String> order, Integer offset, Integer limit);

    List<SoftwareDetailModel> search(Long groupId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                    Long beginTime, Long endTime, Integer offset, Integer limit);

    int count(Long groupId, Boolean isDisabled, Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime);

    PageModel<SoftwareDetailModel> page(Collection<Long> ids, String mode, Long groupId, Long userId, Long searchUserId,Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                       Collection<Long> tags,
                                       Integer minComment, Integer maxComment,
                                       Integer minView, Integer maxView,
                                        Integer minSupport,  Integer maxSupport,
                                        Integer minOppose,  Integer maxOppose,
                                       Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order, Integer offset, Integer limit);

    PageModel<SoftwareDetailModel> page(Long groupId, Long searchUserId,Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                       Collection<Long> tags,
                                       Integer minComment, Integer maxComment,
                                       Integer minView, Integer maxView,
                                        Integer minSupport,  Integer maxSupport,
                                        Integer minOppose,  Integer maxOppose,
                                       Long beginTime, Long endTime,
                                       Map<String, String> order, Integer offset, Integer limit);

    PageModel<SoftwareDetailModel> page(Long groupId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                       Long beginTime, Long endTime,
                                       Integer offset,
                                       Integer limit);

    SoftwareDetailModel getDetail(Long id, String mode, Long userId);

    SoftwareDetailModel getDetail(Long id, String mode);

    SoftwareDetailModel getDetail(Long id, Long userId, Long searchUserId);

    SoftwareDetailModel getDetail(Long id, Long userId);

    SoftwareDetailModel getDetail(Long id);
}
