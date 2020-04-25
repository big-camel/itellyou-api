package com.itellyou.service.article;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.view.ViewInfoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ArticleInfoService {
    int insert(ArticleInfoModel articleInfoModel);

    int updateView(Long userId, Long id, Long ip, String os, String browser);

    int updateComments(Long id, Integer value);

    int updateStars(Long id, Integer value);

    int updateMetas(Long id, String customDescription, String cover);

    Long create(Long userId,Long columnId, ArticleSourceType sourceType,String sourceData, String title, String content, String html, String description, List<TagInfoModel> tags, String remark, String save_type, Long ip) throws Exception;

    Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip);

    int updateDeleted(boolean deleted,Long id,Long userId);
}
