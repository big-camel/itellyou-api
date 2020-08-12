package com.itellyou.api.controller.software;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.software.*;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.software.*;
import com.itellyou.service.tag.TagDocService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/software")
public class SoftwareGrabController {

    private final SoftwareGrabService grabService;
    private final SoftwareGroupService groupService;
    private final TagSingleService tagSingleService;
    private final TagDocService tagDocService ;
    private final SoftwareTagService softwareTagService;
    private final SoftwareDocService softwareDocService;
    private final SoftwareAttributesService attributesService;
    private final SoftwareReleaseService releaseService;
    private final SoftwareUpdaterService updaterService;
    private final SoftwareFileService fileService;

    public SoftwareGrabController(SoftwareGrabService grabService, SoftwareGroupService groupService, TagSingleService tagSingleService, TagDocService tagDocService, SoftwareTagService softwareTagService, SoftwareDocService softwareDocService, SoftwareAttributesService attributesService, SoftwareReleaseService releaseService, SoftwareUpdaterService updaterService, SoftwareFileService fileService) {
        this.grabService = grabService;
        this.groupService = groupService;
        this.tagSingleService = tagSingleService;
        this.tagDocService = tagDocService;
        this.softwareTagService = softwareTagService;
        this.softwareDocService = softwareDocService;
        this.attributesService = attributesService;
        this.releaseService = releaseService;
        this.updaterService = updaterService;
        this.fileService = fileService;
    }

    @Transactional
    @PostMapping("/grab")
    public ResultModel grab(HttpServletRequest request, @RequestBody JSONArray params){
        if(params.size() == 0) return new ResultModel(500,"参数错误");

        SoftwareGrabModel prevGrabModel = grabService.findById("itellyou.cn");
        JSONArray prevJsonArray = prevGrabModel != null && StringUtils.isNotEmpty(prevGrabModel.getData()) ? JSONArray.parseArray(prevGrabModel.getData()) : null;
        Map<String,SoftwareGroupModel> softwareGroupModelMap = new HashMap<>();
        Map<String,SoftwareInfoModel> softwareInfoModelMap = new HashMap<>();
        Map<String,Map<String,SoftwareReleaseModel>> softwareReleaseMap = new HashMap<>();
        Map<String,Map<String,Map<String,SoftwareUpdaterModel>>> softwareUpdaterMap = new HashMap<>();
        Map<String,Map<String,Map<String,Map<String,SoftwareFileModel>>>> softwareFileMap = new HashMap<>();
        if(prevJsonArray != null){
            for (Object prevJsonObject : prevJsonArray){
                JSONObject prevJson = (JSONObject)prevJsonObject;
                // 分组
                JSONObject prevGroupJson = prevJson.getJSONObject("group");
                SoftwareGroupModel groupModel = prevGroupJson.toJavaObject(SoftwareGroupModel.class);
                if(!softwareGroupModelMap.containsKey(groupModel.getName())) {
                    softwareGroupModelMap.put(groupModel.getName(),groupModel);
                }
                // 软件信息
                SoftwareInfoModel softwareInfoModel = new SoftwareInfoModel();
                String softName = prevJson.getString("name");
                softwareInfoModel.setName(softName);
                softwareInfoModel.setId(prevJson.getLong("id"));
                softwareInfoModelMap.put(softName,softwareInfoModel);
                // 版本信息
                if(!softwareReleaseMap.containsKey(softName)){
                    softwareReleaseMap.put(softName,new HashMap<>());
                }
                if(!softwareUpdaterMap.containsKey(softName)){
                    softwareUpdaterMap.put(softName,new HashMap<>());
                }
                if(!softwareFileMap.containsKey(softName)){
                    softwareFileMap.put(softName,new HashMap<>());
                }
                JSONArray versionsJSONArray = prevJson.getJSONArray("versions");
                for (Object versionsJSONObject : versionsJSONArray){
                    JSONObject versionsJson = (JSONObject)versionsJSONObject;
                    String versionsName = versionsJson.getString("name");
                    SoftwareReleaseModel releaseModel = new SoftwareReleaseModel();
                    releaseModel.setId(versionsJson.getLong("id"));
                    releaseModel.setName(versionsName);
                    releaseModel.setSoftwareId(prevJson.getLong("id"));
                    softwareReleaseMap.get(softName).put(versionsName,releaseModel);

                    // 设置 Updater
                    if(!softwareUpdaterMap.get(softName).containsKey(versionsName)){
                        softwareUpdaterMap.get(softName).put(versionsName,new HashMap<>());
                    }
                    if(!softwareFileMap.get(softName).containsKey(versionsName)){
                        softwareFileMap.get(softName).put(versionsName,new HashMap<>());
                    }
                    JSONArray updaterJSONArray = versionsJson.getJSONArray("versionUpdaters");
                    for (Object updaterJSONObject : updaterJSONArray){
                        JSONObject updaterJson = (JSONObject)updaterJSONObject;
                        String updaterName = updaterJson.getString("name");
                        SoftwareUpdaterModel updaterModel = new SoftwareUpdaterModel();
                        updaterModel.setId(updaterJson.getLong("id"));
                        updaterModel.setName(updaterName);
                        updaterModel.setReleaseId(releaseModel.getId());
                        softwareUpdaterMap.get(softName).get(versionsName).put(updaterName,updaterModel);

                        if(!softwareFileMap.get(softName).get(versionsName).containsKey(updaterName)){
                            softwareFileMap.get(softName).get(versionsName).put(updaterName,new HashMap<>());
                        }
                        // 设置 File
                        JSONArray filesJSONArray = updaterJson.getJSONArray("files");
                        for (Object fileJSONObject : filesJSONArray) {
                            JSONObject fileJson = (JSONObject) fileJSONObject;
                            String fileName = fileJson.getString("name");
                            SoftwareFileModel fileModel = new SoftwareFileModel();
                            fileModel.setUpdaterId(updaterModel.getId());
                            fileModel.setId(fileJson.getLong("id"));
                            fileModel.setName(fileName);
                            softwareFileMap.get(softName).get(versionsName).get(updaterName).put(fileName,fileModel);
                        }
                    }
                }
            }
        }
        Long ip = IPUtils.toLong(request);
        Long userId = 10016L;
        try{
            for (Object jsonObject: params) {
                JSONObject json = (JSONObject)jsonObject;
                String name = json.getString("name");
                String tag = json.getString("tag");
                JSONObject groupJson = json.getJSONObject("group");
                SoftwareGroupModel groupModel = groupJson.toJavaObject(SoftwareGroupModel.class);
                // 未有记录分组，则写入分组
                if(!softwareGroupModelMap.containsKey(groupModel.getName())){
                    groupModel.setCreatedIp(ip);
                    groupModel.setCreatedUserId(userId);
                    groupModel.setCreatedTime(DateUtils.getTimestamp());
                    groupService.add(groupModel);
                    softwareGroupModelMap.put(groupModel.getName(),groupModel);
                    if(!groupModel.getId().equals(0l)) groupJson.put("id",groupModel.getId());
                }else{
                    groupModel = softwareGroupModelMap.get(groupModel.getName());
                }
                // 未有标签则创建标签
                TagInfoModel tagInfoModel = tagSingleService.findByName(tag);
                if(tagInfoModel == null){
                    tagInfoModel = new TagInfoModel();
                    Long tagId = tagDocService.create(userId,tag,"","","",tag,"","user",ip);
                    tagInfoModel.setId(tagId);
                }
                Long tagId = tagInfoModel.getId();
                Long softId;
                // 软件信息不存在则录入
                if(!softwareInfoModelMap.containsKey(name)){
                    softId = softwareDocService.create(userId,groupModel.getId(),name,name,name,name,new HashSet<Long>(){{ add(tagId); }},"采集创建","user",ip,true,true);
                    json.put("id",softId);
                }else{
                    softId = softwareInfoModelMap.get(name).getId();
                }
                // 设置属性
                JSONObject attributesJson = json.getJSONObject("attributes");
                HashSet<SoftwareAttributesModel> attributesModels = new HashSet<>();
                for(String attributesKey : attributesJson.keySet()){
                    SoftwareAttributesModel attributesModel = new SoftwareAttributesModel();
                    attributesModel.setSoftwareId(softId);
                    attributesModel.setName(attributesKey);
                    attributesModel.setValue(attributesJson.getString(attributesKey));
                    attributesModel.setCreatedTime(DateUtils.getTimestamp());
                    attributesModel.setCreatedUserId(userId);
                    attributesModel.setCreatedIp(ip);
                    attributesModels.add(attributesModel);
                }
                // 从未发布过软件才要设置属性
                if(attributesModels.size() > 0 && !softwareInfoModelMap.containsKey(name)){
                    attributesService.addAll(attributesModels);
                }
                // 设置Release和Updater
                JSONArray versionsJSONArray = json.getJSONArray("versions");
                for (Object versionsJSONObject : versionsJSONArray){
                    JSONObject versionsJson = (JSONObject)versionsJSONObject;
                    String versionsName = versionsJson.getString("name");
                    SoftwareReleaseModel releaseModel = new SoftwareReleaseModel();
                    if(softwareReleaseMap.containsKey(name) && softwareReleaseMap.getOrDefault(name,new HashMap<>()).containsKey(versionsName)){
                        releaseModel = softwareReleaseMap.get(name).get(versionsName);
                    }else{
                        releaseModel.setName(versionsName);
                        releaseModel.setCreatedIp(ip);
                        releaseModel.setCreatedTime(DateUtils.getTimestamp());
                        releaseModel.setCreatedUserId(userId);
                        releaseModel.setSoftwareId(softId);
                        releaseService.add(releaseModel);
                        versionsJson.put("id",releaseModel.getId());
                    }

                    // 设置 Updater
                    JSONArray updaterJSONArray = versionsJson.getJSONArray("versionUpdaters");
                    for (Object updaterJSONObject : updaterJSONArray){
                        JSONObject updaterJson = (JSONObject)updaterJSONObject;
                        String updaterName = updaterJson.getString("name");
                        SoftwareUpdaterModel updaterModel = new SoftwareUpdaterModel();
                        if(softwareUpdaterMap.getOrDefault(name,new HashMap<>()).getOrDefault(versionsName,new HashMap<>()).containsKey(updaterName)){
                            updaterModel = softwareUpdaterMap.get(name).get(versionsName).get(updaterName);
                        }else{
                            updaterModel.setName(updaterName);
                            updaterModel.setCreatedIp(ip);
                            updaterModel.setCreatedTime(DateUtils.getTimestamp());
                            updaterModel.setCreatedUserId(userId);
                            updaterModel.setReleaseId(releaseModel.getId());
                            updaterService.add(updaterModel);
                            updaterJson.put("id",updaterModel.getId());
                        }

                        // 设置 File
                        JSONArray filesJSONArray = updaterJson.getJSONArray("files");
                        for (Object fileJSONObject : filesJSONArray) {
                            JSONObject fileJson = (JSONObject) fileJSONObject;
                            String fileName = fileJson.getString("name");
                            SoftwareFileModel fileModel = new SoftwareFileModel();
                            if(softwareFileMap.getOrDefault(name,new HashMap<>()).getOrDefault(versionsName,new HashMap<>()).getOrDefault(updaterName,new HashMap<>()).containsKey(fileName)){
                                fileModel = softwareFileMap.get(name).get(versionsName).get(updaterName).get(fileName);
                            }else{
                                fileModel.setUpdaterId(updaterModel.getId());
                                fileModel.setId(fileJson.getLong("id"));
                                fileModel.setTitle(fileJson.getString("title"));
                                fileModel.setName(fileName);
                                fileModel.setSuffix(fileJson.getString("suffix"));
                                fileModel.setSha1(fileJson.getString("sha1"));
                                fileModel.setSha256(fileJson.getString("sha256"));
                                fileModel.setMd5(fileJson.getString("md5"));
                                fileModel.setSize(Long.parseLong(fileJson.getOrDefault("size",0).toString()));
                                String publishDateString = fileJson.getString("publishDate");
                                if(StringUtils.isNotEmpty(publishDateString)){
                                    String publishDateValue = publishDateString + (publishDateString.indexOf(":") > 0 ? "" : " 00:00:00");
                                    LocalDateTime publishDate = DateUtils.formatToDateTime(publishDateValue);
                                    fileModel.setPublishDate(DateUtils.getTimestamp(publishDate));
                                }
                                fileModel.setEd2k(fileJson.getString("ed2k"));
                                fileModel.setMagnet(fileJson.getString("magnet"));
                                fileModel.setCreatedIp(ip);
                                fileModel.setCreatedTime(DateUtils.getTimestamp());
                                fileModel.setCreatedUserId(userId);
                                fileService.add(fileModel);
                            }
                        }
                    }

                }
            }
            prevGrabModel.setData(params.toJSONString());
            grabService.update(prevGrabModel);
            return new ResultModel();
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResultModel(500,e.getMessage());
        }
    }
}
