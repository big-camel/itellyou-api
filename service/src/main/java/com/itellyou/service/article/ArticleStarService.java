package com.itellyou.service.article;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleStarDetailModel;
import com.itellyou.model.article.ArticleStarModel;

import java.util.List;
import java.util.Map;

public interface ArticleStarService {
    int insert(ArticleStarModel model) throws Exception;
    int delete(Long articleId, Long userId) throws Exception;
    List<ArticleStarDetailModel> search(Long articleId, Long userId,
                                         Long beginTime, Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
    int count(Long articleId, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<ArticleStarDetailModel> page(Long articleId, Long userId,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);
}
