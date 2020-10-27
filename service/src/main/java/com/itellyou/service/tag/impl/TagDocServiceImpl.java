package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.TagEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.tag.*;
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

@Service
public class TagDocServiceImpl implements TagDocService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagInfoDao infoDao;
    private final TagSearchService searchService;
    private final UserDraftService draftService;
    private final TagInfoService tagInfoService;
    private final TagSingleService singleService;
    private final TagVersionService versionService;
    private final OperationalPublisher operationalPublisher;

    public TagDocServiceImpl(TagInfoDao infoDao, TagSearchService searchService, UserDraftService draftService, TagInfoService tagInfoService, TagSingleService singleService, TagVersionService versionService, OperationalPublisher operationalPublisher) {
        this.infoDao = infoDao;
        this.searchService = searchService;
        this.draftService = draftService;
        this.tagInfoService = tagInfoService;
        this.singleService = singleService;
        this.versionService = versionService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    public Long create(Long userId,String name, String content, String html,String icon, String description, String remark, String save_type, Long ip) throws Exception {
        try{
            TagInfoModel infoModel = new TagInfoModel();
            infoModel.setName(name);
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.toLocalDateTime());
            infoModel.setCreatedUserId(userId);
            int resultRows = tagInfoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入标签失败");
            RedisUtils.set(CacheKeys.TAG_KEY,infoModel.getId(),infoModel);
            TagVersionModel versionModel = addVersion(infoModel.getId(),userId,content,html,icon,description,remark,1,save_type,ip,true,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.remove(CacheKeys.TAG_KEY,infoModel.getId());
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.TAG_KEY,key = "#id")
    public TagVersionModel addVersion(Long id, Long userId, String content, String html,String icon, String description, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            TagVersionModel versionModel = new TagVersionModel();
            versionModel.setTagId(id);
            TagDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            // 刚创建，没有任何版本信息
            if(detailModel == null)
            {
                // 获取文章基本信息
                TagInfoModel infoModel = singleService.findById(id);
                if(infoModel != null){
                    // 实例化一个详细信息的文章
                    detailModel = new TagDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setGroupId(infoModel.getGroupId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }else throw new Exception("文章不存在");
            }
            //初始化原版本内容
            versionModel.setContent(detailModel.getContent());
            versionModel.setHtml(detailModel.getHtml());
            versionModel.setDescription(detailModel.getDescription());
            versionModel.setIcon(detailModel.getIcon());
            //判断是否需要新增版本
            boolean isAdd = force;
            // 强制更新
            if (force) {
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else if (StringUtils.isNotEmpty(content)&& !content.equals(versionModel.getContent())) {
                versionModel.setContent(content);
                versionModel.setHtml(html);
                isAdd = true;
            }
            // 当有新内容更新时才需要新增版本
            if (isAdd) {
                if(isPublish != true && description != null) {
                    versionModel.setDescription(description);
                }
                if(icon != null)versionModel.setIcon(icon);
                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.toLocalDateTime());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int result = versionService.insert(versionModel);
                if(result < 1) throw new Exception("写入版本失败");
                result = infoDao.updateVersionById(id,isPublish ? versionModel.getVersion() : null,versionModel.getVersion(),isPublish,DateUtils.getTimestamp(),ip,userId);
                if(result != 1) throw new Exception("更新版本失败");

                if(isPublish){
                    // 更新冗余信息
                    tagInfoService.updateInfo(id,versionModel.getDescription(),DateUtils.getTimestamp(),ip,userId);
                }
            }
            String url = "/tag/" + id.toString();
            String draftTitle = detailModel.getName();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.TAG,id,DateUtils.toLocalDateTime(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()){
                    operationalPublisher.publish(new TagEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
                }else{
                    operationalPublisher.publish(new TagEvent(this, EntityAction.UPDATE,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
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
