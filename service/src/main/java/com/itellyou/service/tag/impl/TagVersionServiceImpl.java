package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.dao.tag.TagVersionDao;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.TagEvent;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.tag.TagVersionService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagVersionServiceImpl implements TagVersionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagVersionDao versionDao;
    private final TagInfoDao infoDao;
    private final TagSearchService searchService;
    private final UserDraftService draftService;

    private final OperationalPublisher operationalPublisher;

    @Autowired
    public TagVersionServiceImpl(TagVersionDao versionDao, TagInfoDao infoDao, TagSearchService searchService, UserDraftService draftService, OperationalPublisher operationalPublisher){
        this.versionDao = versionDao;
        this.infoDao = infoDao;
        this.searchService = searchService;
        this.draftService = draftService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @CacheEvict(value = "tag",key = "#versionModel.tagId")
    public int insert(TagVersionModel versionModel) {
        try{
            int rows = versionDao.insert(versionModel);
            if(rows != 1){
                return rows;
            }
            Integer version = findVersionById(versionModel.getId());
            versionModel.setVersion(version);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    @CacheEvict(value = "tag",key = "#versionModel.tagId")
    public int update(TagVersionModel versionModel) {
        try{
            int rows = versionDao.update(versionModel);
            if(rows != 1){
                return rows;
            }
            if(versionModel.getVersion() == null || versionModel.getVersion() <= 0 ){
                Integer version = findVersionById(versionModel.getId());
                versionModel.setVersion(version);
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<TagVersionModel> searchByTagId(Long tagId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return versionDao.search(null,tagId,null,hasContent,null,null,null,null,null,null,order,null,null);

    }

    @Override
    public List<TagVersionModel> searchByTagId(Long tagId) {
        return searchByTagId(tagId,false);
    }

    @Override
    public int count(Long id, Long tagId, String userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(id,tagId,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public TagVersionModel findById(Long id) {
        return findByTagIdAndId(id,null);
    }

    @Override
    public TagVersionModel findByTagIdAndId(Long id, Long tagId) {
        List<TagVersionModel> list = versionDao.search(id,tagId,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    @CacheEvict(value = "tag",key = "#id")
    public int updateVersionById(Long id, Integer version, Integer draft, Boolean isPublished, Long time, Long ip, Long userId) {
        return infoDao.updateVersionById(id,version,draft,isPublished,time,ip,userId);
    }

    @Override
    public int updateVersion(Long id, Integer version, Boolean isPublished, Long time, Long ip, Long userId) {
        return updateVersionById(id,version,null,isPublished,time,ip,userId);
    }

    @Override
    @Transactional
    public int updateVersion(TagVersionModel versionModel) {
        try{
            Long versionId = versionModel.getId();
            int result = versionId == null || versionId.equals(0l) ? insert(versionModel) : update(versionModel);
            if(result != 1)
            {
                throw new Exception("更新版本失败");
            }
            result = updateVersionById(versionModel.getTagId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long id, Integer draft, Boolean isPublished, Long time, Long ip, Long userId) {
        return updateVersionById(id,null,draft,isPublished,time,ip,userId);
    }

    @Override
    @Transactional
    public int updateDraft(TagVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }

            result = updateDraft(versionModel.getTagId(),versionModel.getVersion(),null,versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新草稿版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }


    @Override
    @Transactional
    @CacheEvict(value = "tag",key = "#id")
    public TagVersionModel addVersion(Long id, Long userId, String content, String html,String icon, String description, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            TagDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            if(detailModel == null){
                TagInfoModel infoModel = searchService.findById(id);
                if(infoModel != null){
                    detailModel = new TagDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setName(infoModel.getName());
                    detailModel.setDescription(description);
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }
            }
            TagVersionModel versionModel = new TagVersionModel();
            versionModel.setTagId(id);
            if (force) {
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                if (StringUtils.isNotEmpty(content) && detailModel != null && !content.equals(detailModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                }
            }
            if(detailModel == null || detailModel.isDisabled()){
                return null;
            }

            // 当有新内容更新时才需要新增版本
            if (StringUtils.isNotEmpty(versionModel.getContent())) {
                if(isPublish == true) {
                    versionModel.setDescription(detailModel.getDescription());
                }else{
                    versionModel.setDescription(description == null ? detailModel.getDescription() : description);
                }
                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setIcon(icon == null ? detailModel.getIcon() : icon);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.getTimestamp());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int rows = isPublish == true ? updateVersion(versionModel) : updateDraft(versionModel);
                if (rows != 1)
                    throw new Exception("新增版本失败");
            }
            String url = "/tag/" + id.toString();
            String draftTitle = detailModel.getName();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.TAG,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()){
                    operationalPublisher.publish(new TagEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
                }else{
                    operationalPublisher.publish(new TagEvent(this, EntityAction.UPDATE,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
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
