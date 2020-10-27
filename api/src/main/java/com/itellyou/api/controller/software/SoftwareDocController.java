package com.itellyou.api.controller.software;

import com.itellyou.model.software.*;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.software.*;
import com.itellyou.service.collab.CollabInfoService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.service.user.UserSingleService;
import com.itellyou.util.DateUtils;
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
import java.util.*;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/software")
public class SoftwareDocController {
    private final SoftwareInfoService softwareInfoService;
    private final SoftwareSearchService softwareSearchService;
    private final SoftwareSingleService softwareSingleService;
    private final SoftwareVersionSearchService softwareVersionSearchService;
    private final SoftwareDocService softwareDocService;
    private final CollabInfoService collabInfoService;
    private final UserSingleService userSearchService;
    private final TagSingleService tagSingleService;
    private final SoftwareAttributesService attributesService;

    @Autowired
    public SoftwareDocController(SoftwareInfoService softwareInfoService, SoftwareSearchService softwareSearchService, SoftwareSingleService softwareSingleService, SoftwareVersionSearchService softwareVersionSearchService, SoftwareDocService softwareDocService, CollabInfoService collabInfoService, UserSingleService userSearchService, TagSingleService tagSingleService, SoftwareAttributesService attributesService){
        this.softwareInfoService = softwareInfoService;
        this.softwareSearchService = softwareSearchService;
        this.softwareVersionSearchService = softwareVersionSearchService;
        this.softwareSingleService = softwareSingleService;
        this.softwareDocService = softwareDocService;
        this.collabInfoService = collabInfoService;
        this.userSearchService = userSearchService;
        this.tagSingleService = tagSingleService;
        this.attributesService = attributesService;
    }

    @PostMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userInfoModel, @MultiRequestBody(required = false) Long groupId, @MultiRequestBody(required = false) String name, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);

        try{
            Long id = softwareDocService.create(userInfoModel.getId(),groupId,name,content,html, StringUtils.getFragmenter(content),null
                    ,"创建软件",save_type,ipLong);
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
        SoftwareDetailModel softwareDetailModel = softwareSearchService.getDetail(id,"draft",userInfoModel.getId());
        if(softwareDetailModel == null || softwareDetailModel.isDisabled() || softwareDetailModel.isDeleted()){
            return  new ResultModel(404,"无记录，错误的ID");
        }

        //暂时只能创建者有权限编辑
        if(!softwareDetailModel.getCreatedUserId().equals(userInfoModel.getId())){
            return new ResultModel(401,"无权限编辑");
        }
        if(ot){
            String clientIp = IPUtils.getClientIp(request);
            String key = "software/" + softwareDetailModel.getId();
            CollabInfoModel collabInfoModel = collabInfoService.createDefault(key,userInfoModel.getId(),clientIp);
            if(collabInfoModel == null){
                return new ResultModel(0,"创建协作失败");
            }
            softwareDetailModel.setCollab(collabInfoModel);
        }
        return new ResultModel(softwareDetailModel,new Labels.LabelModel(SoftwareDetailModel.class,"draft","collab"));
    }

    @PutMapping("/{id:\\d+}/content")
    public ResultModel content(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody(required = false) String name, @MultiRequestBody(required = false , value = "group_id") Long groupId,  @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);
        SoftwareInfoModel infoModel = softwareSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用文章");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new ResultModel(401,"无权限编辑");
            }

            SoftwareVersionModel versionModel = softwareDocService.addVersion(id,userInfoModel.getId(),groupId,name,content,html,StringUtils.getFragmenter(content),null,
                    "一般编辑更新",null,save_type,ipLong,false,false);
            if(versionModel == null) return new ResultModel(0,"更新内容失败");
            SoftwareDetailModel detailModel = softwareSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(SoftwareDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/meta")
    public ResultModel meta(UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody(required = false, value = "custom_description") String customDescription, @MultiRequestBody(required = false) String logo){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }

        SoftwareInfoModel infoModel = softwareSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用问题");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new ResultModel(401,"无权限编辑");
            }

            int result = softwareInfoService.updateMetas(id,customDescription,logo);
            if(result != 1) return new ResultModel(0,"更新失败");
            SoftwareDetailModel detailModel = softwareSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(SoftwareDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/attributes")
    public ResultModel attributes(UserInfoModel userInfoModel,HttpServletRequest request, @PathVariable Long id, @RequestBody Map<String,String> params){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }

        SoftwareInfoModel infoModel = softwareSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用软件");
            //暂时只能创建者有权限编辑
            if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
                return new ResultModel(401,"无权限编辑");
            }
            attributesService.clear(id);
            HashSet<SoftwareAttributesModel> attributesModels = new HashSet<>();
            for (Map.Entry<String,String> entry : params.entrySet()) {
                SoftwareAttributesModel attributesModel = new SoftwareAttributesModel();
                attributesModel.setName(entry.getKey());
                attributesModel.setValue(entry.getValue());
                attributesModel.setCreatedIp(IPUtils.toLong(request));
                attributesModel.setCreatedUserId(userInfoModel.getId());
                attributesModel.setCreatedTime(DateUtils.toLocalDateTime());
                attributesModels.add(attributesModel);
            }
            if(attributesModels.size() > 0){
                int result = attributesService.addAll(attributesModels);
                if(result != attributesModels.size()) return new ResultModel(0,"更新失败");
            }
            SoftwareDetailModel detailModel = softwareSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(SoftwareDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/rollback")
    public ResultModel rollback(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody Long version_id){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        SoftwareInfoModel infoModel = softwareSingleService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用提问");
        //暂时只能创建者有权限编辑
        if(!infoModel.getCreatedUserId().equals(userInfoModel.getId())){
            return new ResultModel(401,"无权限编辑");
        }
        SoftwareVersionDetailModel softwareVersion = softwareVersionSearchService.getDetail(version_id);
        if(softwareVersion == null || softwareVersion.isDisabled() || !softwareVersion.getSoftwareId().equals(id)){
            return  new ResultModel(0,"无记录，错误的ID");
        }
        String clientIp = IPUtils.getClientIp(request);
        try {
            Collection<Long> tagIds = softwareVersion.getTags().stream().map(TagDetailModel::getId).collect(Collectors.toSet());
            SoftwareVersionModel versionModel = softwareDocService.addVersion(id,userInfoModel.getId(),softwareVersion.getGroupId(),
                    softwareVersion.getName(),softwareVersion.getContent(),softwareVersion.getHtml(),softwareVersion.getDescription(),tagIds,
                    "回滚到版本[" + softwareVersion.getVersion() + "]",null,"rollback",
                    IPUtils.toLong(clientIp),false,true);

            if(versionModel == null) return new ResultModel(0,"回滚失败");
            SoftwareDetailModel detailModel = softwareSearchService.getDetail(id,"draft",userInfoModel.getId());
            return new ResultModel(detailModel,new Labels.LabelModel(SoftwareDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/publish")
    public ResultModel publish(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody @NotNull Long groupId, @MultiRequestBody(required = false) String sourceType, @MultiRequestBody(required = false) String sourceData, @MultiRequestBody @NotNull Long[] tags, @MultiRequestBody(required = false) String remark){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        SoftwareDetailModel softwareVersion = softwareSearchService.getDetail(id,"draft",userInfoModel.getId());
        if(softwareVersion == null || softwareVersion.isDisabled() || softwareVersion.isDeleted()){
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
            String description = softwareVersion.getDescription();
            SoftwareVersionModel versionModel = softwareDocService.addVersion(id,userInfoModel.getId(),groupId,
                    softwareVersion.getName(),softwareVersion.getContent(),softwareVersion.getHtml(),description,tagIds,
                    remark,null,"publish",
                    IPUtils.toLong(clientIp),true,true);

            if(versionModel == null) return new ResultModel(0,"发布文章失败");
            SoftwareDetailModel detailModel = softwareSearchService.getDetail(id,"version",userInfoModel.getId());
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
        SoftwareInfoModel infoModel = softwareSingleService.findById(id);
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
