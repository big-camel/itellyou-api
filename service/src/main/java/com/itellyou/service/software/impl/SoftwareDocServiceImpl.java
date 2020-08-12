package com.itellyou.service.software.impl;

import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.software.SoftwareGroupModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.software.SoftwareVersionModel;
import com.itellyou.model.event.SoftwareEvent;
import com.itellyou.model.event.TagIndexEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.software.*;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class SoftwareDocServiceImpl implements SoftwareDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SoftwareSearchService searchService;
    private final SoftwareSingleService singleService;
    private final SoftwareInfoService infoService;
    private final SoftwareVersionService versionService;
    private final SoftwareTagService softwareTagService;
    private final UserDraftService draftService;
    private final OperationalPublisher operationalPublisher;

    public SoftwareDocServiceImpl(SoftwareSearchService searchService, SoftwareSingleService singleService, SoftwareInfoService infoService, SoftwareVersionService versionService, SoftwareTagService softwareTagService, UserDraftService draftService, OperationalPublisher operationalPublisher) {
        this.searchService = searchService;
        this.singleService = singleService;
        this.infoService = infoService;
        this.versionService = versionService;
        this.softwareTagService = softwareTagService;
        this.draftService = draftService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public Long create(Long userId, Long groupId, String name, String content, String html, String description, HashSet<Long> tagIds, String remark, String save_type, Long ip) throws Exception {
        return create(userId,groupId,name,content,html,description,tagIds,remark,save_type,ip,false,true);
    }

    @Override
    @Transactional
    public Long create(Long userId,Long groupId, String name, String content, String html, String description, HashSet<Long> tagIds, String remark, String save_type, Long ip, Boolean isPublish, Boolean force) {
        try{
            SoftwareInfoModel infoModel = new SoftwareInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.getTimestamp());
            infoModel.setCreatedUserId(userId);
            int resultRows = infoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入失败");
            SoftwareVersionModel versionModel = addVersion(infoModel.getId(),userId,groupId,name,content,html,description,tagIds,remark,1,save_type,ip,isPublish,force);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.removeCache("software",infoModel.getId());
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    private HashSet<Long> findNotExistTag(HashSet<Long> source,HashSet<Long> target){
        HashSet<Long> list = new LinkedHashSet<>();
        for(Long sourceTag:source){
            boolean exist = false;
            if(target != null){
                for(Long targetTag:target){
                    if(sourceTag.equals(targetTag)){
                        exist = true;
                        break;
                    }
                }
            }
            if(!exist){
                list.add(sourceTag);
            }
        }
        return list;
    }

    @Override
    @Transactional
    @CacheEvict(value = "software",key = "#id")
    public SoftwareVersionModel addVersion(Long id, Long userId, Long groupId, String name, String content, String html, String description, HashSet<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            SoftwareVersionModel versionModel = new SoftwareVersionModel();
            versionModel.setSoftwareId(id);
            SoftwareDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            // 刚创建，没有任何版本信息
            if(detailModel == null)
            {
                // 获取文章基本信息
                SoftwareInfoModel infoModel = singleService.findById(id);
                if(infoModel != null){
                    // 实例化一个详细信息的文章
                    detailModel = new SoftwareDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }
            }
            // 强制更新
            if (force) {
                versionModel.setName(name);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                // 判断标题是否更改
                if (StringUtils.isNotEmpty(name) && !name.equals(detailModel.getName())) {
                    versionModel.setName(name);
                }
                // 判断内容是否更改
                if (StringUtils.isNotEmpty(content)&& !content.equals(detailModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                    if(!StringUtils.isNotEmpty(versionModel.getName())){
                        versionModel.setName(detailModel.getName());
                    }
                }else if(StringUtils.isNotEmpty(versionModel.getName())){
                    versionModel.setContent(detailModel.getContent());
                    versionModel.setHtml(detailModel.getHtml());
                }
            }
            if(detailModel == null) return null;

            HashSet<Long> oldTags = new HashSet<>();
            for (TagDetailModel tagDetail : detailModel.getTags()){
                oldTags.add(tagDetail.getId());
            }
            // 查找新添加的标签
            HashSet<Long> addTags = tagIds != null ? findNotExistTag(tagIds,oldTags) : new HashSet<>();
            // 查找需要删除的标签
            HashSet<Long> delTags = tagIds != null ? findNotExistTag(oldTags,tagIds) : new HashSet<>();
            // 当有新内容更新时才需要新增版本
            List<TagDetailModel> tagDetailModels = new ArrayList<>();
            if (tagIds != null) {
                for (Long tagId : tagIds){
                    TagDetailModel tagDetailModel = new TagDetailModel();
                    tagDetailModel.setId(tagId);
                    tagDetailModels.add(tagDetailModel);
                }
            }
            if (StringUtils.isNotEmpty(versionModel.getName())|| StringUtils.isNotEmpty(versionModel.getContent())) {
                if(isPublish == true){
                    versionModel.setDescription(detailModel.getDescription());
                    // 设置标签，并更新标签信息
                    versionModel.setTags(tagDetailModels);
                    // 更新专栏信息
                    versionModel.setGroupId(groupId);
                    // 更新文章冗余信息
                    infoService.updateInfo(id,versionModel.getName(),versionModel.getDescription(),versionModel.getGroupId(),DateUtils.getTimestamp(),ip,userId);
                    // 更新文章冗余标签
                    softwareTagService.clear(id);
                    if(tagIds.size() > 0)
                        softwareTagService.addAll(id,tagIds);
                }else{
                    // 设置版本信息
                    versionModel.setDescription(description == null ? detailModel.getDescription() : description);
                    versionModel.setTags(tagIds == null ?  detailModel.getTags() : tagDetailModels);
                    SoftwareGroupModel groupModel = detailModel.getGroup();
                    versionModel.setGroupId(groupId == null ? (groupModel != null ? groupModel.getId() : 0) : groupId );
                }
                // 设置版本信息
                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.getTimestamp());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);
                // 更新版本
                int rows = isPublish == true ? versionService.updateVersion(versionModel) : versionService.updateDraft(versionModel);
                if (rows != 1)
                    throw new Exception("新增版本失败");
            }
            // 设置或更新用户草稿
            String draftTitle = versionModel.getName();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),"/software/" + id.toString(),draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.SOFTWARE,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                // 提交文章操作发布
                if(!detailModel.isPublished()) {
                    // 首次发布
                    operationalPublisher.publish(new SoftwareEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.getTimestamp(), ip));
                }else{
                    // 文章更新
                    operationalPublisher.publish(new SoftwareEvent(this, EntityAction.UPDATE,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.getTimestamp(), ip));
                }
                // 提交标签操作发布
                HashSet<Long> publishTagIds = new HashSet<>();
                if(addTags != null && addTags.size() > 0){
                    for(Long i : addTags){
                        if(!publishTagIds.contains(i)) publishTagIds.add(i);
                    }
                }
                if(delTags != null && delTags.size() > 0){
                    for(Long i : delTags){
                        if(!publishTagIds.contains(i)) publishTagIds.add(i);
                    }
                }
                if(publishTagIds.size() > 0){
                    operationalPublisher.publish(new TagIndexEvent(this,publishTagIds));
                }
            }
            return versionModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

}
