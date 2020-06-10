package com.itellyou.service.article;

import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.sys.VoteType;

public interface ArticleInfoService {
    int insert(ArticleInfoModel articleInfoModel);

    int updateView(Long userId, Long id, Long ip, String os, String browser);

    int updateComments(Long id, Integer value);

    int updateStars(Long id, Integer value);

    int updateMetas(Long id, String customDescription, String cover);

    int updateVote(VoteType type,Integer value,Long id);

    int updateDeleted(boolean deleted, Long id,Long userId,Long ip);

    int updateInfo(Long id, String title, String description, Long columnId, ArticleSourceType sourceType, String sourceData,Long time,
                   Long ip,
                   Long userId);
}
