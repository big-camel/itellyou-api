package com.itellyou.service.article.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.event.ColumnIndexEvent;
import com.itellyou.model.event.TagIndexEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.article.*;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
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
public class ArticleDocServiceImpl implements ArticleDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleSearchService searchService;
    private final ArticleSingleService singleService;
    private final ArticleInfoService infoService;
    private final ArticleVersionService versionService;
    private final ArticleTagService articleTagService;
    private final TagInfoService tagService;
    private final UserDraftService draftService;
    private final ColumnInfoService columnService;
    private final UserInfoService userService;
    private final OperationalPublisher operationalPublisher;

    public ArticleDocServiceImpl(ArticleSearchService searchService, ArticleSingleService singleService, ArticleInfoService infoService, ArticleVersionService versionService, ArticleTagService articleTagService, TagInfoService tagService, UserDraftService draftService, ColumnInfoService columnService, UserInfoService userService, OperationalPublisher operationalPublisher) {
        this.searchService = searchService;
        this.singleService = singleService;
        this.infoService = infoService;
        this.versionService = versionService;
        this.articleTagService = articleTagService;
        this.tagService = tagService;
        this.draftService = draftService;
        this.columnService = columnService;
        this.userService = userService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    public Long create(Long userId,Long columnId, ArticleSourceType sourceType,String sourceData, String title, String content, String html, String description, HashSet<Long> tagIds, String remark, String save_type, Long ip) {
        try{
            ArticleInfoModel infoModel = new ArticleInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.getTimestamp());
            infoModel.setCreatedUserId(userId);
            int resultRows = infoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入文章失败");
            ArticleVersionModel versionModel = addVersion(infoModel.getId(),userId,columnId,sourceType,sourceData,title,content,html,description,tagIds,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
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
    @CacheEvict(value = "article",key = "#id")
    public ArticleVersionModel addVersion(Long id, Long userId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, HashSet<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            ArticleVersionModel versionModel = new ArticleVersionModel();
            versionModel.setArticleId(id);
            ArticleDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            // 刚创建，没有任何版本信息
            if(detailModel == null)
            {
                // 获取文章基本信息
                ArticleInfoModel infoModel = singleService.findById(id);
                if(infoModel != null){
                    // 实例化一个详细信息的文章
                    detailModel = new ArticleDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }
            }
            // 强制更新
            if (force) {
                versionModel.setTitle(title);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                // 判断标题是否更改
                if (StringUtils.isNotEmpty(title) && !title.equals(detailModel.getTitle())) {
                    versionModel.setTitle(title);
                }
                // 判断内容是否更改
                if (StringUtils.isNotEmpty(content)&& !content.equals(detailModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                    if(!StringUtils.isNotEmpty(versionModel.getTitle())){
                        versionModel.setTitle(detailModel.getTitle());
                    }
                }else if(StringUtils.isNotEmpty(versionModel.getTitle())){
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
            if (StringUtils.isNotEmpty(versionModel.getTitle())|| StringUtils.isNotEmpty(versionModel.getContent())) {
                if(isPublish == true){
                    versionModel.setDescription(detailModel.getDescription());
                    // 设置标签，并更新标签信息
                    versionModel.setTags(tagDetailModels);

                    if(addTags != null && addTags.size() > 0){
                        int result = tagService.updateArticleCountById(addTags,1);
                        if(result != addTags.size()) throw new Exception("更新标签文章数量失败");
                    }
                    if(delTags != null && delTags.size() > 0){
                        int result = tagService.updateArticleCountById(delTags,-1);
                        if(result != delTags.size()) throw new Exception("更新标签文章数量失败");
                    }
                    // 更新专栏信息
                    versionModel.setColumnId(columnId);
                    ColumnInfoModel column = detailModel.getColumn();
                    if(columnId > 0 && (column == null || !column.getId().equals(columnId))){
                        int result = columnService.updateArticles(columnId,1);
                        if(result != 1) throw new Exception("更新专栏文章数量失败");
                    }

                    if(column != null && !column.getId().equals(columnId) && column.getId() > 0){
                        int result = columnService.updateArticles(column.getId(),-1);
                        if(result != 1) throw new Exception("更新专栏文章数量失败");
                    }
                    // 设置文章来源信息
                    if(sourceType == ArticleSourceType.ORIGINAL){
                        sourceData = "";
                    }
                    versionModel.setSourceType(sourceType);
                    versionModel.setSourceData(sourceData);
                    // 如果未设置封面，则自动提取
                    if(StringUtils.isEmpty(detailModel.getCover())){
                        JSONObject coverObj = StringUtils.getEditorContentCover(content);
                        if(coverObj != null){
                            int result = infoService.updateMetas(id,null,coverObj.getString("src"));
                            if(result != 1) throw new Exception("更新封面失败");
                        }
                    }
                    // 更新文章冗余信息
                    infoService.updateInfo(id,versionModel.getTitle(),versionModel.getDescription(),versionModel.getColumnId(),versionModel.getSourceType(),versionModel.getSourceData(),DateUtils.getTimestamp(),ip,userId);
                    // 更新文章冗余标签
                    articleTagService.clear(id);
                    if(tagIds.size() > 0)
                        articleTagService.addAll(id,tagIds);
                }else{
                    // 设置版本信息
                    versionModel.setDescription(description == null ? detailModel.getDescription() : description);
                    versionModel.setTags(tagIds == null ?  detailModel.getTags() : tagDetailModels);
                    versionModel.setSourceType(sourceType == null ? detailModel.getSourceType() : sourceType);
                    versionModel.setSourceData(sourceData == null ? detailModel.getSourceData() : sourceData);
                    ColumnInfoModel column = detailModel.getColumn();
                    versionModel.setColumnId(columnId == null ? (column != null ? column.getId() : 0) : columnId );
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
                if(isPublish == true && detailModel.isPublished() == false) rows = userService.updateArticleCount(detailModel.getCreatedUserId(),1);
                if(rows != 1) throw new Exception("更新用户文章数量失败");
            }
            // 设置或更新用户草稿
            String draftTitle = versionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),"/article/" + id.toString(),draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.ARTICLE,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                // 提交文章操作发布
                if(!detailModel.isPublished()) {
                    // 首次发布
                    operationalPublisher.publish(new ArticleEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.getTimestamp(), ip));
                }else{
                    // 文章更新
                    operationalPublisher.publish(new ArticleEvent(this, EntityAction.UPDATE,
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
                // 提交专栏操作发布
                HashSet<Long> columns = new HashSet<>();
                ColumnInfoModel column = detailModel.getColumn();
                if(columnId > 0 && (column == null || column.getId() != columnId)){
                    columns.add(columnId);
                }
                if(column != null && column.getId() != columnId && column.getId() > 0){
                    columns.add(column.getId());
                }

                if(columns.size() > 0){
                    operationalPublisher.publish(new ColumnIndexEvent(this,columns));
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
