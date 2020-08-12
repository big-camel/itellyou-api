package com.itellyou.service.software;

import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.sys.VoteType;

public interface SoftwareInfoService {
    int insert(SoftwareInfoModel softwareInfoModel);

    int updateView(Long userId, Long id, Long ip, String os, String browser);

    int updateComments(Long id, Integer value);

    int updateVote(VoteType type, Integer value, Long id);

    int updateMetas(Long id, String customDescription, String logo);

    int updateDeleted(boolean deleted, Long id, Long userId, Long ip);

    int updateInfo(Long id, String name, String description, Long groupId, Long time,
                   Long ip,
                   Long userId);
}
