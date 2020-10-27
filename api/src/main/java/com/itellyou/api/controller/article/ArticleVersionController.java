package com.itellyou.api.controller.article;

import com.itellyou.api.handler.TokenAccessDeniedException;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionDetailModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticlePaidReadSearchService;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.article.ArticleVersionSearchService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/article/{articleId:\\d+}/version")
public class ArticleVersionController {
    private final ArticleVersionSearchService versionService;
    private final ColumnSearchService columnSearchService;
    private final ArticlePaidReadSearchService paidReadSearchService;
    private final ArticleSingleService articleSearchService;

    @Autowired
    public ArticleVersionController(ArticleVersionSearchService versionService, ColumnSearchService columnSearchService, ArticlePaidReadSearchService paidReadSearchService, ArticleSingleService articleSearchService){
        this.versionService = versionService;
        this.columnSearchService = columnSearchService;
        this.paidReadSearchService = paidReadSearchService;
        this.articleSearchService = articleSearchService;
    }

    private void check(Long articleId,Long userId){
        ArticleInfoModel articleInfoModel = articleSearchService.findById(articleId);
        if(articleInfoModel == null) throw new TokenAccessDeniedException(403,"403");
        boolean check = paidReadSearchService.checkRead(paidReadSearchService.findByArticleId(articleId),articleInfoModel.getCreatedUserId(),userId);
        if(check == false){
            throw new TokenAccessDeniedException(403,"无权限");
        }
    }

    @GetMapping("")
    public ResultModel list(UserInfoModel userModel,@PathVariable @NotNull Long articleId){
        check(articleId,userModel == null ? null : userModel.getId());
        List<ArticleVersionDetailModel> listVersion = versionService.searchByArticleId(articleId);
        return new ResultModel(listVersion,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    @GetMapping("/{versionId:\\d+}")
    public ResultModel find(UserInfoModel userModel,@PathVariable @NotNull Long versionId, @PathVariable @NotNull Long articleId){
        ArticleVersionDetailModel versionModel = versionService.getDetail(versionId);
        if(versionModel == null || !versionModel.getArticleId().equals(articleId)){
            return new ResultModel(0,"错误的编号");
        }
        check(articleId,userModel == null ? null : userModel.getId());
        return new ResultModel(versionModel,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    private String getVersionHtml(ArticleVersionDetailModel versionModel){
        StringBuilder currentString = new StringBuilder("<div>");
        currentString.append("<h2>" + versionModel.getTitle() + "</h2>");
        List<TagDetailModel> currentTagList = versionModel.getTags();
        currentString.append("<p class=\"info-layout\">");
        if(currentTagList != null && currentTagList.size() > 0){
            for(TagDetailModel tagInfo : currentTagList){
                currentString.append("<span>" + tagInfo.getName() + "</span>");
            }
            currentString.append("，");
        }
        currentString.append("</p>");
        Long columnId = versionModel.getColumnId();
        ColumnInfoModel columnModel = null;
        if(columnId != 0){
            columnModel = columnSearchService.findById(columnId);
            if(columnModel == null){
                columnModel = new ColumnInfoModel();
                columnModel.setName("未知专栏");
            }
        }
        currentString.append("<p>发布到:" + ( columnId.equals(0l) ? "个人文章" : columnModel.getName() ) + "</p>");
        ArticleSourceType sourceType = versionModel.getSourceType();
        String sourceText = "";
        switch (sourceType){
            case ORIGINAL:
                sourceText = "原创";
                break;
            case REPRODUCED:
                sourceText = "转载";
                break;
            case TRANSLATION:
                sourceText = "翻译";
                break;
        }
        currentString.append("<p>来源:" + (sourceType != ArticleSourceType.ORIGINAL ? sourceText + "[" + versionModel.getSourceData() + "]" : sourceText) + "</p>");
        currentString.append("</div>");
        currentString.append(versionModel.getHtml());

        return currentString.toString();
    }

    @GetMapping("/{current:\\d+}...{target:\\d+}")
    public ResultModel compare(UserInfoModel userModel,@PathVariable @NotNull Long current, @PathVariable @NotNull Long target, @PathVariable @NotNull Long articleId){
        check(articleId,userModel == null ? null : userModel.getId());
        ArticleVersionDetailModel currentVersion = versionService.getDetail(current);
        if(currentVersion == null || !currentVersion.getArticleId().equals(articleId)){
            return new ResultModel(0,"错误的当前编号");
        }

        ArticleVersionDetailModel targetVersion = versionService.getDetail(target);
        if(targetVersion == null || !targetVersion.getArticleId().equals(articleId)){
            return new ResultModel(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new ResultModel(htmlData);
    }
}
