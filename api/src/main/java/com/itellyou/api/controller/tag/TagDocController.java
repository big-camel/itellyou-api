package com.itellyou.api.controller.tag;

import com.itellyou.api.handler.TokenAccessDeniedException;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.collab.CollabInfoService;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.tag.TagVersionService;
import com.itellyou.service.user.UserPermissionService;
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
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/tag")
public class TagDocController {

    private final TagInfoService tagService;
    private final TagSearchService searchService;
    private final TagVersionService versionService;
    private final CollabInfoService collabService;
    private final UserSearchService userSearchService;
    private final UserPermissionService userPermissionService;

    @Autowired
    public TagDocController(TagInfoService tagService, TagSearchService searchService, CollabInfoService collabService, TagVersionService versionService, UserSearchService userSearchService, UserPermissionService userPermissionService){
        this.tagService = tagService;
        this.searchService = searchService;
        this.collabService = collabService;
        this.versionService = versionService;
        this.userSearchService = userSearchService;
        this.userPermissionService = userPermissionService;
    }

    private boolean checkAuthority(Long authorId,Long userId){
        if(!authorId.equals(userId) && !userPermissionService.check(userId,"web_tag_public_edit")){
            throw new TokenAccessDeniedException(403,"无权限编辑");
        }
        return true;
    }

    @PostMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userInfoModel, @MultiRequestBody @NotBlank String name, @MultiRequestBody @NotBlank String content, @MultiRequestBody @NotBlank String html, @MultiRequestBody(required = false) String icon, @MultiRequestBody(required = false) String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        save_type = StringUtils.isNotEmpty(save_type) ? save_type : "user";
        TagInfoModel tagModel = searchService.findByName(name);
        if(tagModel != null) return new ResultModel(1001,"标签不可用");

        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);

        try{
            Long id = tagService.create(userInfoModel.getId(),name,content,html,icon,StringUtils.getFragmenter(content), "创建标签",save_type,ipLong);
            if(id == null) return new ResultModel(0,"创建失败");
            return new ResultModel(id);
        }catch (Exception e){
            return new ResultModel(0,"创建失败，" + e.getMessage());
        }
    }

    @GetMapping("/{id:\\d+}/edit")
    public ResultModel draft(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @RequestParam(required = false) boolean ot){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        TagDetailModel detailModel = searchService.getDetail(id,"draft",null);
        if(detailModel == null || detailModel.isDisabled()){
            return  new ResultModel(404,"无记录，错误的ID");
        }

        if(!checkAuthority(detailModel.getCreatedUserId(),userInfoModel.getId())){
            return new ResultModel(403,"无权限编辑");
        }

        if(ot){
            String clientIp = IPUtils.getClientIp(request);
            String key = "tag/" + detailModel.getId();
            CollabInfoModel collabInfoModel = collabService.createDefault(key,userInfoModel.getId(),clientIp);
            if(collabInfoModel == null){
                return new ResultModel(0,"创建协作失败");
            }
            detailModel.setCollab(collabInfoModel);
        }
        return new ResultModel(detailModel,new Labels.LabelModel(TagDetailModel.class,"draft","collab"));
    }

    @PutMapping("/{id:\\d+}/content")
    public ResultModel content(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody(required = false) String icon, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);
        TagInfoModel infoModel = searchService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled()) return new ResultModel(404,"无可用标签");
            // 创建者和具有公共编辑权限的用户才能编辑
            if(!checkAuthority(infoModel.getCreatedUserId(),userInfoModel.getId())){
                return new ResultModel(403,"无权限编辑");
            }

            TagVersionModel versionModel = versionService.addVersion(id,userInfoModel.getId(),content,html,icon,StringUtils.getFragmenter(content),
                    "一般编辑更新",null,save_type,ipLong,false,false);
            if(versionModel == null) return new ResultModel(0,"更新内容失败");
            TagDetailModel detailModel = searchService.getDetail(id,"draft",null);
            return new ResultModel(detailModel,new Labels.LabelModel(TagDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/rollback")
    public ResultModel rollback(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody Long version_id){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        TagInfoModel infoModel = searchService.findById(id);
        if(infoModel == null || infoModel.isDisabled()) return new ResultModel(404,"无可用提问");
        // 创建者和具有公共编辑权限的用户才能编辑
        if(!checkAuthority(infoModel.getCreatedUserId(),userInfoModel.getId())){
            return new ResultModel(403,"无权限编辑");
        }
        TagVersionModel tagVersion = versionService.findByTagIdAndId(version_id,id);
        if(tagVersion == null || tagVersion.isDisabled()){
            return  new ResultModel(0,"无记录，错误的ID");
        }
        String clientIp = IPUtils.getClientIp(request);
        try {
            TagVersionModel versionModel = versionService.addVersion(id,userInfoModel.getId(),tagVersion.getContent(),tagVersion.getHtml(),tagVersion.getIcon(),tagVersion.getDescription(),
                    "回滚到版本[" + tagVersion.getVersion() + "]",null,"rollback",
                    IPUtils.toLong(clientIp),false,true);

            if(versionModel == null) return new ResultModel(0,"回滚失败");
            TagDetailModel detailModel = searchService.getDetail(id,"draft",null);
            return new ResultModel(detailModel,new Labels.LabelModel(TagDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/publish")
    public ResultModel publish(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody(required = false) String remark){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        TagDetailModel detailModel = searchService.getDetail(id,"draft",null);
        if(detailModel == null || detailModel.isDisabled()){
            return new ResultModel(401,"无权限");
        }
        // 创建者和具有公共编辑权限的用户才能编辑
        if(!checkAuthority(detailModel.getCreatedUserId(),userInfoModel.getId())){
            return new ResultModel(403,"无权限编辑");
        }
        String clientIp = IPUtils.getClientIp(request);
        try {
            TagVersionModel versionModel = versionService.addVersion(id, userInfoModel.getId(), detailModel.getContent(), detailModel.getHtml(),detailModel.getIcon(),null,
                    remark, null,"publish", IPUtils.toLong(clientIp),true,true);
            if(versionModel == null) return new ResultModel(0,"发布失败");
        }catch (Exception e){
            return new ResultModel(0,"发布失败");
        }
        detailModel = searchService.getDetail(id,"version",null);
        return new ResultModel(detailModel);
    }

    @PostMapping("/{id:\\d+}/collab")
    public ResultModel collab(@MultiRequestBody @NotBlank String token, @PathVariable Long id){
        CollabInfoModel collabInfoModel = collabService.findByToken(token);
        if(collabInfoModel == null || collabInfoModel.isDisabled() == true){
            return new ResultModel(0,"错误的Token");
        }
        TagInfoModel infoModel = searchService.findById(id);
        if(infoModel == null || infoModel.isDisabled()){
            return new ResultModel(0,"没有可用的文档");
        }

        UserInfoModel userInfo = userSearchService.find(collabInfoModel.getCreatedUserId(),null);
        if(userInfo == null || userInfo.isDisabled()){
            return new ResultModel(0,"用户状态不正确");
        }

        // 创建者和具有公共编辑权限的用户才能编辑
        if(!checkAuthority(infoModel.getCreatedUserId(),userInfo.getId())){
            return new ResultModel(403,"无权限编辑");
        }

        Map<String,Object> mapData = new HashMap<>();
        mapData.put("doc",infoModel);
        mapData.put("user",userInfo);
        return new ResultModel(mapData);
    }
}
