package com.itellyou.api.controller.article;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.collab.CollabInfoService;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleVersionService;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/article")
public class ArticleDocController {
    private final ArticleInfoService articleInfoService;
    private final ArticleSearchService articleSearchService;
    private final ArticleVersionService articleVersionService;
    private final CollabInfoService collabInfoService;
    private final ArticleVersionService versionService;
    private final TagSearchService tagSearchService;
    private final UserSearchService userSearchService;

    @Autowired
    public ArticleDocController(ArticleInfoService articleInfoService,ArticleVersionService articleVersionService,ArticleSearchService articleSearchService, CollabInfoService collabInfoService, ArticleVersionService versionService,TagSearchService tagSearchService, UserSearchService userSearchService){
        this.articleInfoService = articleInfoService;
        this.articleSearchService = articleSearchService;
        this.articleVersionService = articleVersionService;
        this.collabInfoService = collabInfoService;
        this.versionService = versionService;
        this.tagSearchService = tagSearchService;
        this.userSearchService = userSearchService;
    }

    @PostMapping("/create")
    public Result create(HttpServletRequest request, UserInfoModel userInfoModel, @MultiRequestBody(required = false) Long columnId, @MultiRequestBody(required = false) String sourceType, @MultiRequestBody(required = false) String sourceData, @MultiRequestBody(required = false) String title, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new Result(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);

        try{
            if(sourceType == null) sourceType = ArticleSourceType.ORIGINAL.toString();
            Long id = articleInfoService.create(userInfoModel.getId(),columnId,ArticleSourceType.valueOf(sourceType.toUpperCase()),sourceData,title,content,html, StringUtils.getFragmenter(content),null
                    ,"创建文章",save_type,ipLong);
            if(id == null) return new Result(0,"创建失败");
            return new Result(id);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(0,"创建失败，" + e.getMessage());
        }
    }

    @GetMapping("/{id:\\d+}/edit")
    public Result draft(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @RequestParam(required = false) boolean ot){
        if(userInfoModel == null){
            return new Result(401,"未登录");
        }
        ArticleDetailModel articleDetailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
        if(articleDetailModel == null || articleDetailModel.isDisabled() || articleDetailModel.isDeleted()){
            return  new Result(404,"无记录，错误的ID");
        }

        //暂时只能创建者有权限编辑
        if(!articleDetailModel.getCreatedUserId().equals(userInfoModel.getId())){
            return new Result(401,"无权限编辑");
        }
        if(ot){
            String clientIp = IPUtils.getClientIp(request);
            String key = "article/" + articleDetailModel.getId();
            CollabInfoModel collabInfoModel = collabInfoService.createDefault(key,userInfoModel.getId(),clientIp);
            if(collabInfoModel == null){
                return new Result(0,"创建协作失败");
            }
            articleDetailModel.setCollab(collabInfoModel);
        }
        return new Result(articleDetailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft","collab"));
    }

    @PutMapping("/{id:\\d+}/content")
    public Result content(HttpServletRequest request,UserInfoModel userInfoModel,@PathVariable Long id,@MultiRequestBody(required = false) String title,@MultiRequestBody String content,@MultiRequestBody String html,@MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new Result(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);
        ArticleInfoModel infoModel = articleSearchService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new Result(404,"无可用文章");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new Result(401,"无权限编辑");
            }

            ArticleVersionModel versionModel = articleVersionService.addVersion(id,userInfoModel.getId(),null,null,null,title,content,html,StringUtils.getFragmenter(content),null,
                    "一般编辑更新",null,save_type,ipLong,false,false);
            if(versionModel == null) return new Result(0,"更新内容失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new Result(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/meta")
    public Result meta(HttpServletRequest request,UserInfoModel userInfoModel,@PathVariable Long id,@MultiRequestBody(required = false, value = "custom_description") String customDescription,@MultiRequestBody(required = false) String cover){
        if(userInfoModel == null){
            return new Result(401,"未登录");
        }
        if(StringUtils.isEmpty(customDescription) && StringUtils.isEmpty(cover)){
            return new Result(500,"参数错误");
        }
        ArticleInfoModel infoModel = articleSearchService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new Result(404,"无可用问题");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new Result(401,"无权限编辑");
            }

            int result = articleInfoService.updateMetas(id,customDescription,cover);
            if(result != 1) return new Result(0,"更新失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new Result(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/rollback")
    public Result rollback(HttpServletRequest request,UserInfoModel userInfoModel,@PathVariable Long id,@MultiRequestBody Long version_id){
        if(userInfoModel == null){
            return new Result(401,"未登录");
        }
        ArticleInfoModel infoModel = articleSearchService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new Result(404,"无可用提问");
        //暂时只能创建者有权限编辑
        if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
            return new Result(401,"无权限编辑");
        }
        ArticleVersionModel articleVersion = versionService.findByArticleIdAndId(version_id,id);
        if(articleVersion == null || articleVersion.isDisabled()){
            return  new Result(0,"无记录，错误的ID");
        }
        String clientIp = IPUtils.getClientIp(request);
        try {
            ArticleVersionModel versionModel = articleVersionService.addVersion(id,userInfoModel.getId(),articleVersion.getColumnId(),articleVersion.getSourceType(),articleVersion.getSourceData(),
                    articleVersion.getTitle(),articleVersion.getContent(),articleVersion.getHtml(),articleVersion.getDescription(),articleVersion.getTags(),
                    "回滚到版本[" + articleVersion.getVersion() + "]",null,"rollback",
                    IPUtils.toLong(clientIp),false,true);

            if(versionModel == null) return new Result(0,"回滚失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new Result(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/publish")
    public Result publish(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody @NotNull Long columnId, @MultiRequestBody(required = false) String sourceType, @MultiRequestBody(required = false) String sourceData, @MultiRequestBody @NotNull Long[] tags, @MultiRequestBody(required = false) String remark){
        if(userInfoModel == null){
            return new Result(401,"未登录");
        }
        ArticleDetailModel articleVersion = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
        if(articleVersion == null || articleVersion.isDisabled() || articleVersion.isDeleted()){
            return new Result(401,"无权限");
        }

        if(tags.length > 0){
            int rows = tagSearchService.exists(tags);
            if(rows != tags.length){
                return new Result(0,"标签数据错误");
            }
        }

        List<TagInfoModel> listTag = new ArrayList<>();
        for(Object tagId : tags){
            TagInfoModel tag = new TagInfoModel();
            tag.setId((Long) tagId);
            listTag.add(tag);
        }

        String clientIp = IPUtils.getClientIp(request);
        try {
            ArticleVersionModel versionModel = articleVersionService.addVersion(id,userInfoModel.getId(),columnId,ArticleSourceType.valueOf(sourceType.toUpperCase()),sourceData,
                    articleVersion.getTitle(),articleVersion.getContent(),articleVersion.getHtml(),articleVersion.getDescription(),listTag,
                    remark,null,"publish",
                    IPUtils.toLong(clientIp),true,true);

            if(versionModel == null) return new Result(0,"发布文章失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"version",userInfoModel.getId());
            return new Result(detailModel);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @PostMapping("/collab")
    public Result collab(@MultiRequestBody @NotBlank String token, @PathVariable Long id){
        CollabInfoModel collabInfoModel = collabInfoService.findByToken(token);
        if(collabInfoModel == null || collabInfoModel.isDisabled() == true){
            return new Result(0,"错误的Token");
        }
        ArticleInfoModel infoModel = articleSearchService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()){
            return new Result(0,"错误的文档");
        }
        UserInfoModel userInfo = userSearchService.findById(collabInfoModel.getCreatedUserId());
        if(userInfo == null || userInfo.isDisabled()){
            return new Result(0,"用户状态不正确");
        }
        Map<String,Object> mapData = new HashMap<>();
        mapData.put("doc",infoModel);
        mapData.put("user",userInfo);
        return new Result(mapData);
    }
}
