package com.itellyou.service.article.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.constant.CacheKeys;
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
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
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
public class ArticleDocServiceImpl implements ArticleDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArticleInfoDao infoDao;
    private final ArticleSearchService searchService;
    private final ArticleSingleService singleService;
    private final ArticleInfoService infoService;
    private final ArticleVersionService versionService;
    private final ArticleTagService articleTagService;
    private final ArticleVersionTagService versionTagService;
    private final TagInfoService tagService;
    private final TagSingleService tagSingleService;
    private final UserDraftService draftService;
    private final ColumnInfoService columnService;
    private final UserInfoService userService;
    private final OperationalPublisher operationalPublisher;

    public ArticleDocServiceImpl(ArticleInfoDao infoDao, ArticleSearchService searchService, ArticleSingleService singleService, ArticleInfoService infoService, ArticleVersionService versionService, ArticleTagService articleTagService, ArticleVersionTagService versionTagService, TagInfoService tagService, TagSingleService tagSingleService, UserDraftService draftService, ColumnInfoService columnService, UserInfoService userService, OperationalPublisher operationalPublisher) {
        this.infoDao = infoDao;
        this.searchService = searchService;
        this.singleService = singleService;
        this.infoService = infoService;
        this.versionService = versionService;
        this.articleTagService = articleTagService;
        this.versionTagService = versionTagService;
        this.tagService = tagService;
        this.tagSingleService = tagSingleService;
        this.draftService = draftService;
        this.columnService = columnService;
        this.userService = userService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    public Long create(Long userId,Long columnId, ArticleSourceType sourceType,String sourceData, String title, String content, String html, String description, Collection<Long> tagIds, String remark, String save_type, Long ip) {
        try{
            ArticleInfoModel infoModel = new ArticleInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.toLocalDateTime());
            infoModel.setCreatedUserId(userId);
            int resultRows = infoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入文章失败");
            RedisUtils.set(CacheKeys.ARTICLE_KEY,infoModel.getId(),infoModel);
            ArticleVersionModel versionModel = addVersion(infoModel.getId(),userId,columnId,sourceType,sourceData,title,content,html,description,tagIds,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.remove(CacheKeys.ARTICLE_KEY,infoModel.getId());
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.ARTICLE_KEY,key = "#id")
    public ArticleVersionModel addVersion(Long id, Long userId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, Collection<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
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
                }else throw new Exception("文章不存在");
            }
            //初始化原版本内容
            versionModel.setTitle(detailModel.getTitle());
            versionModel.setContent(detailModel.getContent());
            versionModel.setHtml(detailModel.getHtml());
            versionModel.setDescription(detailModel.getDescription());
            versionModel.setSourceType(detailModel.getSourceType());
            versionModel.setSourceData(detailModel.getSourceData());
            versionModel.setColumnId(detailModel.getColumn() != null ? detailModel.getColumn().getId() : 0);
            //判断是否需要新增版本
            boolean isAdd = force;
            // 强制更新
            if (force) {
                versionModel.setTitle(title);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                // 判断标题是否更改
                if (StringUtils.isNotEmpty(title) && !title.equals(versionModel.getTitle())) {
                    versionModel.setTitle(title);
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
                    // 更新标签信息
                    if(addTags != null && addTags.size() > 0){
                        int result = tagService.updateArticleCountById(addTags,1);
                        if(result != addTags.size()) throw new Exception("更新标签文章数量失败");
                    }
                    if(delTags != null && delTags.size() > 0){
                        int result = tagService.updateArticleCountById(delTags,-1);
                        if(result != delTags.size()) throw new Exception("更新标签文章数量失败");
                    }
                    // 发布需要强制更新专栏信息
                    versionModel.setColumnId(columnId);
                    ColumnInfoModel column = detailModel.getColumn();
                    if(columnId > 0 && (column == null || !column.getId().equals(columnId))){
                        int result = columnService.updateArticles(columnId,1);
                        if(result != 1) throw new Exception("更新专栏文章数量失败");
                    }
                    // 更新专栏信息
                    if(column != null && !column.getId().equals(columnId) && column.getId() > 0){
                        int result = columnService.updateArticles(column.getId(),-1);
                        if(result != 1) throw new Exception("更新专栏文章数量失败");
                    }
                    // 发布需要强制设置文章来源信息
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
                    // 设置版本信息，未发布
                    if(description != null)versionModel.setDescription(description);
                    if(sourceType != null)versionModel.setSourceType(sourceType);
                    if(sourceData != null)versionModel.setSourceData(sourceData);
                    ColumnInfoModel column = detailModel.getColumn();
                    if(columnId != null)versionModel.setColumnId(columnId );
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
                // 如果文章之前从未发布过，那么更新用户的文章数量+1
                if(isPublish == true && detailModel.isPublished() == false) {
                    result = userService.updateArticleCount(detailModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户文章数量失败");
                }
            }
            // 设置或更新用户草稿
            String draftTitle = versionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),"/article/" + id.toString(),draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.ARTICLE,id,DateUtils.toLocalDateTime(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                // 提交文章操作发布
                if(!detailModel.isPublished()) {
                    // 首次发布
                    operationalPublisher.publish(new ArticleEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.toLocalDateTime(), ip));
                }else{
                    // 文章更新
                    operationalPublisher.publish(new ArticleEvent(this, EntityAction.UPDATE,
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
                // 提交专栏操作发布
                Collection<Long> columns = new HashSet<>();
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
