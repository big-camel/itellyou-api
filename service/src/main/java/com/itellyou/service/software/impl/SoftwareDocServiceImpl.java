package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareInfoDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.SoftwareEvent;
import com.itellyou.model.event.TagIndexEvent;
import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.software.SoftwareVersionModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.software.*;
import com.itellyou.service.tag.TagSingleService;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SoftwareDocServiceImpl implements SoftwareDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SoftwareInfoDao infoDao;
    private final SoftwareSearchService searchService;
    private final SoftwareSingleService singleService;
    private final SoftwareInfoService infoService;
    private final SoftwareVersionService versionService;
    private final SoftwareTagService softwareTagService;
    private final UserDraftService draftService;
    private final OperationalPublisher operationalPublisher;
    private final TagSingleService tagSingleService;
    private final SoftwareVersionTagService versionTagService;

    public SoftwareDocServiceImpl(SoftwareInfoDao infoDao, SoftwareSearchService searchService, SoftwareSingleService singleService, SoftwareInfoService infoService, SoftwareVersionService versionService, SoftwareTagService softwareTagService, UserDraftService draftService, OperationalPublisher operationalPublisher, TagSingleService tagSingleService, SoftwareVersionTagService versionTagService) {
        this.infoDao = infoDao;
        this.searchService = searchService;
        this.singleService = singleService;
        this.infoService = infoService;
        this.versionService = versionService;
        this.softwareTagService = softwareTagService;
        this.draftService = draftService;
        this.operationalPublisher = operationalPublisher;
        this.tagSingleService = tagSingleService;
        this.versionTagService = versionTagService;
    }

    @Override
    public Long create(Long userId, Long groupId, String name, String content, String html, String description, Collection<Long> tagIds, String remark, String save_type, Long ip) throws Exception {
        return create(userId,groupId,name,content,html,description,tagIds,remark,save_type,ip,false,true);
    }

    @Override
    @Transactional
    public Long create(Long userId,Long groupId, String name, String content, String html, String description, Collection<Long> tagIds, String remark, String save_type, Long ip, Boolean isPublish, Boolean force) {
        try{
            SoftwareInfoModel infoModel = new SoftwareInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.toLocalDateTime());
            infoModel.setCreatedUserId(userId);
            int resultRows = infoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入失败");
            RedisUtils.set(CacheKeys.SOFTWARE_KEY,infoModel.getId(),infoModel);
            SoftwareVersionModel versionModel = addVersion(infoModel.getId(),userId,groupId,name,content,html,description,tagIds,remark,1,save_type,ip,isPublish,force);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.remove(CacheKeys.SOFTWARE_KEY,infoModel.getId());
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.SOFTWARE_KEY,key = "#id")
    public SoftwareVersionModel addVersion(Long id, Long userId, Long groupId, String name, String content, String html, String description, Collection<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
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
                }else throw new Exception("软件不存在");
            }
            //初始化原版本内容
            versionModel.setName(detailModel.getName());
            versionModel.setContent(detailModel.getContent());
            versionModel.setHtml(detailModel.getHtml());
            versionModel.setDescription(detailModel.getDescription());
            versionModel.setGroupId(detailModel.getGroupId());
            versionModel.setLogo(detailModel.getLogo());
            //判断是否需要新增版本
            boolean isAdd = force;
            // 强制更新
            if (force) {
                versionModel.setName(name);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                // 判断标题是否更改
                if (StringUtils.isNotEmpty(name) && !name.equals(versionModel.getName())) {
                    versionModel.setName(name);
                    isAdd = true;
                }
                // 如果内容有更改，设置新的内容
                if (StringUtils.isNotEmpty(content)&& !content.equals(versionModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                    isAdd = true;
                }
            }

            Collection<Long> oldTagIds = detailModel.getTags().stream().map(TagDetailModel::getId).collect(Collectors.toSet());
            // 查找新添加的标签
            Collection<Long> addTags = tagIds != null ? tagSingleService.findNotExist(tagIds,oldTagIds) : new HashSet<>();
            // 查找需要删除的标签
            Collection<Long> delTags = tagIds != null ? tagSingleService.findNotExist(oldTagIds,tagIds) : new HashSet<>();
            if (isAdd) {
                if(isPublish == true){
                    // 更新文章冗余信息
                    infoService.updateInfo(id,versionModel.getName(),versionModel.getDescription(),versionModel.getGroupId(),DateUtils.getTimestamp(),ip,userId);
                    // 更新文章冗余标签
                    softwareTagService.clear(id);
                    if(tagIds.size() > 0)
                        softwareTagService.addAll(id,tagIds);
                }else{
                    // 设置版本信息
                    if(description != null )versionModel.setDescription(description);
                    if(groupId != null)versionModel.setGroupId(groupId );
                }
                // 设置版本信息
                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.toLocalDateTime());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int result = versionService.insert(versionModel);
                if(result < 1) throw new Exception("写入版本失败");
                result = infoDao.updateVersion(id,isPublish ? versionModel.getVersion() : null,versionModel.getVersion(),isPublish && !detailModel.isPublished() ? true : null,DateUtils.getTimestamp(),ip,userId);
                if(result != 1) throw new Exception("更新版本失败");
                //增加版本标签，如果没有设置，继承原来的
                versionTagService.addAll(versionModel.getId(),tagIds == null ? oldTagIds : tagIds);
            }
            // 设置或更新用户草稿
            String draftTitle = versionModel.getName();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),"/software/" + id.toString(),draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.SOFTWARE,id,DateUtils.toLocalDateTime(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                // 提交文章操作发布
                if(!detailModel.isPublished()) {
                    // 首次发布
                    operationalPublisher.publish(new SoftwareEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.toLocalDateTime(), ip));
                }else{
                    // 文章更新
                    operationalPublisher.publish(new SoftwareEvent(this, EntityAction.UPDATE,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.toLocalDateTime(), ip));
                }
                // 提交标签操作发布
                Collection<Long> publishTagIds = new HashSet<>();
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
