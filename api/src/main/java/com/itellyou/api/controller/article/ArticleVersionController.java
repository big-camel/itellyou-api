package com.itellyou.api.controller.article;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleVersionService;
import com.itellyou.service.column.ColumnInfoService;
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
    private final ArticleVersionService versionService;
    private final ColumnSearchService columnSearchService;

    @Autowired
    public ArticleVersionController(ArticleVersionService versionService,ColumnSearchService columnSearchService){
        this.versionService = versionService;
        this.columnSearchService = columnSearchService;
    }

    @GetMapping("")
    public Result list(@PathVariable @NotNull Long articleId){

        List<ArticleVersionModel> listVersion = versionService.searchByArticleId(articleId);
        return new Result(listVersion,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    @GetMapping("/{versionId:\\d+}")
    public Result find(@PathVariable @NotNull Long versionId,@PathVariable @NotNull Long articleId){
        ArticleVersionModel versionModel = versionService.findByArticleIdAndId(versionId,articleId);
        if(versionModel == null){
            return new Result(0,"错误的编号");
        }
        return new Result(versionModel,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    private String getVersionHtml(ArticleVersionModel versionModel){
        StringBuilder currentString = new StringBuilder("<div>");
        currentString.append("<h2>" + versionModel.getTitle() + "</h2>");
        List<TagInfoModel> currentTagList = versionModel.getTags();
        currentString.append("<p class=\"info-layout\">");
        if(currentTagList != null && currentTagList.size() > 0){
            for(TagInfoModel tagInfo : currentTagList){
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
    public Result compare(@PathVariable @NotNull Long current,@PathVariable @NotNull Long target,@PathVariable @NotNull Long articleId){
        ArticleVersionModel currentVersion = versionService.findByArticleIdAndId(current,articleId);
        if(currentVersion == null){
            return new Result(0,"错误的当前编号");
        }

        ArticleVersionModel targetVersion = versionService.findByArticleIdAndId(target,articleId);
        if(targetVersion == null){
            return new Result(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new Result(htmlData);
    }
}
