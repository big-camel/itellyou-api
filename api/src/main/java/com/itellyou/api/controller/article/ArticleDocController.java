package com.itellyou.api.controller.article;

import com.itellyou.model.article.*;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.*;
import com.itellyou.service.collab.CollabInfoService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.service.user.UserSingleService;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/article")
public class ArticleDocController {
    private final ArticleInfoService articleInfoService;
    private final ArticleSearchService articleSearchService;
    private final ArticleSingleService articleSingleService;
    private final ArticleVersionSearchService articleVersionSearchService;
    private final ArticleDocService articleDocService;
    private final CollabInfoService collabInfoService;
    private final UserSingleService userSearchService;
    private final ArticlePaidReadService paidReadService;
    private final TagSingleService tagSingleService;

    @Autowired
    public ArticleDocController(ArticleInfoService articleInfoService, ArticleSearchService articleSearchService, ArticleSingleService articleSingleService, ArticleVersionSearchService articleVersionSearchService, ArticleDocService articleDocService, CollabInfoService collabInfoService, UserSingleService userSearchService, ArticlePaidReadService paidReadService, TagSingleService tagSingleService){
        this.articleInfoService = articleInfoService;
        this.articleSearchService = articleSearchService;
        this.articleVersionSearchService = articleVersionSearchService;
        this.articleSingleService = articleSingleService;
        this.articleDocService = articleDocService;
        this.collabInfoService = collabInfoService;
        this.userSearchService = userSearchService;
        this.paidReadService = paidReadService;
        this.tagSingleService = tagSingleService;
    }

    @PostMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userInfoModel, @MultiRequestBody(required = false) Long columnId, @MultiRequestBody(required = false) String sourceType, @MultiRequestBody(required = false) String sourceData, @MultiRequestBody(required = false) String title, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);

        try{
            if(sourceType == null) sourceType = ArticleSourceType.ORIGINAL.toString();
            Long id = articleDocService.create(userInfoModel.getId(),columnId,ArticleSourceType.valueOf(sourceType.toUpperCase()),sourceData,title,content,html, StringUtils.getFragmenter(content),null
                    ,"创建文章",save_type,ipLong);
            if(id == null) return new ResultModel(0,"创建失败");
            return new ResultModel(id);
        }catch (Exception e){
            e.printStackTrace();
            return new ResultModel(0,"创建失败，" + e.getMessage());
        }
    }

    @GetMapping("/{id:\\d+}/edit")
    public ResultModel draft(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @RequestParam(required = false) boolean ot){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        ArticleDetailModel articleDetailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
        if(articleDetailModel == null || articleDetailModel.isDisabled() || articleDetailModel.isDeleted()){
            return  new ResultModel(404,"无记录，错误的ID");
        }

        //暂时只能创建者有权限编辑
        if(!articleDetailModel.getCreatedUserId().equals(userInfoModel.getId())){
            return new ResultModel(401,"无权限编辑");
        }
        if(ot){
            String clientIp = IPUtils.getClientIp(request);
            String key = "article/" + articleDetailModel.getId();
            CollabInfoModel collabInfoModel = collabInfoService.createDefault(key,userInfoModel.getId(),clientIp);
            if(collabInfoModel == null){
                return new ResultModel(0,"创建协作失败");
            }
            articleDetailModel.setCollab(collabInfoModel);
        }
        return new ResultModel(articleDetailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft","collab"));
    }

    @PutMapping("/{id:\\d+}/content")
    public ResultModel content(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody(required = false) String title, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);
        ArticleInfoModel infoModel = articleSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用文章");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new ResultModel(401,"无权限编辑");
            }

            ArticleVersionModel versionModel = articleDocService.addVersion(id,userInfoModel.getId(),null,null,null,title,content,html,StringUtils.getFragmenter(content),null,
                    "一般编辑更新",null,save_type,ipLong,false,false);
            if(versionModel == null) return new ResultModel(0,"更新内容失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/meta")
    public ResultModel meta(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody(required = false, value = "custom_description") String customDescription, @MultiRequestBody(required = false) String cover){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }

        ArticleInfoModel infoModel = articleSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用问题");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new ResultModel(401,"无权限编辑");
            }

            int result = articleInfoService.updateMetas(id,customDescription,cover);
            if(result != 1) return new ResultModel(0,"更新失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/paidread")
    public ResultModel paidRead(UserInfoModel userInfoModel, @PathVariable Long id, @RequestBody Map<String ,Object> params){
        ArticleInfoModel infoModel = articleSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用文章");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new ResultModel(401,"无权限编辑");
            }
            ArticlePaidReadModel paidReadModel = new ArticlePaidReadModel();
            paidReadModel.setArticleId(id);
            paidReadModel.setPaidToRead(false);
            Object paidObject = params.get("paid");
            if(paidObject != null){
                paidReadModel.setPaidToRead(true);
                Map<String,Object> paidMap = (Map<String,Object>)paidObject;
                UserBankType bankType = UserBankType.valueOf(paidMap.get("type").toString().toUpperCase());
                if(bankType.equals(UserBankType.SCORE)) throw new Exception("参数错误");
                paidReadModel.setPaidType(bankType);
                Double amount = Math.abs(Double.valueOf(paidMap.get("amount").toString()));
                paidReadModel.setPaidAmount(amount);
            }
            Object starObject = params.get("star");
            paidReadModel.setStarToRead(false);
            if(starObject != null){
                paidReadModel.setStarToRead(Boolean.valueOf(starObject.toString()));
            }
            Object scaleObject = params.getOrDefault("scale",0);
            if(scaleObject == null) scaleObject = 0;
            Double scale = Math.abs(Double.valueOf(scaleObject.toString()));
            if(scale > 50) scale = 50.0;
            paidReadModel.setFreeReadScale(scale == 0 ? 0 : new BigDecimal(scale).divide(new BigDecimal(100)).doubleValue());
            if(paidReadModel.getStarToRead() || paidReadModel.getPaidToRead()){
                if(paidReadService.insertOrUpdate(paidReadModel) != 1) throw new Exception("设置失败");
            }else{
                paidReadService.deleteByArticleId(id);
            }
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/rollback")
    public ResultModel rollback(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody Long version_id){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        ArticleInfoModel infoModel = articleSingleService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用提问");
        //暂时只能创建者有权限编辑
        if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
            return new ResultModel(401,"无权限编辑");
        }
        ArticleVersionModel articleVersion = articleVersionSearchService.findByArticleIdAndId(version_id,id);
        if(articleVersion == null || articleVersion.isDisabled()){
            return  new ResultModel(0,"无记录，错误的ID");
        }
        String clientIp = IPUtils.getClientIp(request);
        try {
            HashSet<Long> tagIds = new LinkedHashSet<>();
            for(TagDetailModel tagDetailModel : articleVersion.getTags()){
                tagIds.add(tagDetailModel.getId());
            }
            ArticleVersionModel versionModel = articleDocService.addVersion(id,userInfoModel.getId(),articleVersion.getColumnId(),articleVersion.getSourceType(),articleVersion.getSourceData(),
                    articleVersion.getTitle(),articleVersion.getContent(),articleVersion.getHtml(),articleVersion.getDescription(),tagIds,
                    "回滚到版本[" + articleVersion.getVersion() + "]",null,"rollback",
                    IPUtils.toLong(clientIp),false,true);

            if(versionModel == null) return new ResultModel(0,"回滚失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(ArticleDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/publish")
    public ResultModel publish(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody @NotNull Long columnId, @MultiRequestBody(required = false) String sourceType, @MultiRequestBody(required = false) String sourceData, @MultiRequestBody @NotNull Long[] tags, @MultiRequestBody(required = false) String remark){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        ArticleDetailModel articleVersion = articleSearchService.getDetail(id,"draft",userInfoModel.getId());
        if(articleVersion == null || articleVersion.isDisabled() || articleVersion.isDeleted()){
            return new ResultModel(401,"无权限");
        }

        if(tags.length > 0){
            int rows = tagSingleService.exists(tags);
            if(rows != tags.length){
                return new ResultModel(0,"标签数据错误");
            }
        }

        HashSet<Long> tagIds = new LinkedHashSet<>();
        for(Object tagId : tags){
            tagIds.add((Long)tagId);
        }

        String clientIp = IPUtils.getClientIp(request);
        try {
            String description = articleVersion.getDescription();
            ArticleVersionModel versionModel = articleDocService.addVersion(id,userInfoModel.getId(),columnId,ArticleSourceType.valueOf(sourceType.toUpperCase()),sourceData,
                    articleVersion.getTitle(),articleVersion.getContent(),articleVersion.getHtml(),description,tagIds,
                    remark,null,"publish",
                    IPUtils.toLong(clientIp),true,true);

            if(versionModel == null) return new ResultModel(0,"发布文章失败");
            ArticleDetailModel detailModel = articleSearchService.getDetail(id,"version",userInfoModel.getId());
            return new ResultModel(detailModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PostMapping("/collab")
    public ResultModel collab(@MultiRequestBody @NotBlank String token, @PathVariable Long id){
        CollabInfoModel collabInfoModel = collabInfoService.findByToken(token);
        if(collabInfoModel == null || collabInfoModel.isDisabled() == true){
            return new ResultModel(0,"错误的Token");
        }
        ArticleInfoModel infoModel = articleSingleService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()){
            return new ResultModel(0,"错误的文档");
        }
        UserInfoModel userInfo = userSearchService.findById(collabInfoModel.getCreatedUserId());
        if(userInfo == null || userInfo.isDisabled()){
            return new ResultModel(0,"用户状态不正确");
        }
        Map<String,Object> mapData = new HashMap<>();
        mapData.put("doc",infoModel);
        mapData.put("user",userInfo);
        return new ResultModel(mapData);
    }
}
