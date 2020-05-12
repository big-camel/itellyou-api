package com.itellyou.service.article.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.dao.article.ArticleVersionDao;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.event.ColumnIndexEvent;
import com.itellyou.model.event.TagIndexEvent;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.article.ArticleVersionService;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class ArticleVersionServiceImpl implements ArticleVersionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleVersionDao versionDao;
    private final ArticleInfoDao infoDao;
    private final ArticleSearchService searchService;
    private final TagInfoService tagService;
    private final UserDraftService draftService;
    private final ColumnInfoService columnService;
    private final UserInfoService userService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public ArticleVersionServiceImpl(ArticleVersionDao articleVersionDao, ArticleInfoDao infoDao, ArticleSearchService searchService, TagInfoService tagService, UserDraftService draftService, ColumnInfoService columnService, UserInfoService userService, OperationalPublisher operationalPublisher){
        this.versionDao = articleVersionDao;
        this.infoDao = infoDao;
        this.searchService = searchService;
        this.tagService = tagService;
        this.draftService = draftService;
        this.columnService = columnService;
        this.operationalPublisher = operationalPublisher;
        this.userService = userService;
    }

    @Override
    @Transactional
    @CacheEvict(value = "article",key = "#articleVersionModel.articleId")
    public int insert(ArticleVersionModel articleVersionModel) {
        try{
            int rows = versionDao.insert(articleVersionModel);
            if(rows != 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(articleVersionModel.getId());
            articleVersionModel.setVersion(version);
            List<TagInfoModel> tags = articleVersionModel.getTags();
            if(tags != null && tags.size() > 0){
                rows = insertTag(articleVersionModel.getId(),tags);
                if(rows != tags.size()){
                    throw new Exception("写入版本标签失败");
                }
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "article",key = "#versionModel.articleId")
    public int update(ArticleVersionModel versionModel) {
        try{
            int rows = versionDao.update(versionModel);
            if(rows != 1){
                throw new Exception("更新版本失败");
            }
            if(versionModel.getVersion() == null || versionModel.getVersion() <= 0 ){
                Integer version = versionDao.findVersionById(versionModel.getId());
                versionModel.setVersion(version);
            }

            List<TagInfoModel> tags = versionModel.getTags();
            if(tags != null){
                deleteTag(versionModel.getId());
                if(tags.size() > 0){
                    rows = insertTag(versionModel.getId(),tags);
                    if(rows != tags.size()){
                        throw new Exception("更新版本标签失败");
                    }
                }
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int insertTag(Long version, Long tag) {
        TagInfoModel tagInfo = new TagInfoModel();
        tagInfo.setId(tag);
        return versionDao.insertTag(version,tagInfo);
    }

    @Override
    public int insertTag(Long version, List<TagInfoModel> tags) {
        TagInfoModel[] tagArray = new TagInfoModel[tags.size()];
        tags.toArray(tagArray);
        return versionDao.insertTag(version,tagArray);
    }

    @Override
    public int insertTag(Long version, TagInfoModel... tags) {
        return versionDao.insertTag(version,tags);
    }

    @Override
    public int deleteTag(Long version) {
        return versionDao.deleteTag(version);
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<ArticleVersionModel> searchByArticleId(Long articleId,Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return versionDao.search(null,articleId,null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<ArticleVersionModel> searchByArticleId(Long articleId){
        return searchByArticleId(articleId,false);
    }

    @Override
    public ArticleVersionModel findById(Long id) {
        return findByArticleIdAndId(id,null);
    }

    @Override
    public ArticleVersionModel findByArticleIdAndId(Long id, Long articleId) {
        List<ArticleVersionModel> list = versionDao.search(id,articleId,null,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public int updateVersion(Long articleId, Integer version, Long ip, Long user) {
        return updateVersion(articleId,version,false,ip,user);
    }

    @Override
    public int updateVersion(Long articleId, Integer version,Boolean isPublished, Long ip, Long user) {
        return updateVersion(articleId,version,null,isPublished, DateUtils.getTimestamp(),ip,user);
    }

    @Override
    @CacheEvict(value = "article",key = "#articleId")
    public int updateVersion(Long articleId, Integer version, Integer draft,Boolean isPublished, Long time, Long ip, Long user) {
        return infoDao.updateVersion(articleId,version,draft,isPublished,time,ip,user);
    }

    @Override
    @Transactional
    public int updateVersion(ArticleVersionModel versionModel) {
        try{
            Long versionId = versionModel.getId();
            int result = versionId == null || versionId == 0 ? insert(versionModel) : update(versionModel);
            if(result != 1)
            {
                throw new Exception("更新版本失败");
            }
            result = updateVersion(versionModel.getArticleId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
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
    public int updateDraft(Long articleId, Integer version,Boolean isPublished, Long time, Long ip, Long user) {
        return updateVersion(articleId,null,version,isPublished,time,ip,user);
    }

    @Override
    public int updateDraft(Long articleId, Integer version, Long time, Long ip, Long user) {
        return updateDraft(articleId,version,null,time,ip,user);
    }

    @Override
    @Transactional
    public int updateDraft(ArticleVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateDraft(versionModel.getArticleId(),versionModel.getVersion(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
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

    private List<Long> findNotExistTag(List<TagInfoModel> source,List<TagInfoModel> target){
        List<Long> list = new ArrayList<>();
        for(TagInfoModel sourceTag:source){
            boolean exist = false;
            if(target != null){
                for(TagInfoModel targetTag:target){
                    if(sourceTag.getId().equals(targetTag.getId())){
                        exist = true;
                        break;
                    }
                }
            }
            if(!exist){
                list.add(sourceTag.getId());
            }
        }
        return list;
    }
    @Override
    @Transactional
    @CacheEvict(value = "article",key = "#id")
    public ArticleVersionModel addVersion(Long id, Long userId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, List<TagInfoModel> tags, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            ArticleVersionModel versionModel = new ArticleVersionModel();
            versionModel.setArticleId(id);
            ArticleDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            if(detailModel == null)
            {
                ArticleInfoModel infoModel = searchService.findById(id);
                if(infoModel != null){
                    detailModel = new ArticleDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }
            }
            if (force) {
                versionModel.setTitle(title);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                if (StringUtils.isNotEmpty(title) && !title.equals(detailModel.getTitle())) {
                    versionModel.setTitle(title);
                }
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

            List<TagInfoModel> oldTags = new ArrayList<>();
            for (TagDetailModel tagDetail : detailModel.getTags()){
                oldTags.add(new TagInfoModel(tagDetail.getId(),tagDetail.getName(),tagDetail.getGroupId()));
            }
            List<Long> addTags = tags != null ? findNotExistTag(tags,oldTags) : new ArrayList<>();
            List<Long> delTags = tags != null ? findNotExistTag(oldTags,tags) : new ArrayList<>();
            // 当有新内容更新时才需要新增版本
            if (StringUtils.isNotEmpty(versionModel.getTitle())|| StringUtils.isNotEmpty(versionModel.getContent())) {
                if(isPublish == true){
                    versionModel.setDescription(detailModel.getDescription());
                    versionModel.setTags(tags);

                    if(addTags != null && addTags.size() > 0){
                        int result = tagService.updateArticleCountById(addTags,1);
                        if(result != addTags.size()) throw new Exception("更新标签文章数量失败");
                    }
                    if(delTags != null && delTags.size() > 0){
                        int result = tagService.updateArticleCountById(delTags,-1);
                        if(result != delTags.size()) throw new Exception("更新标签文章数量失败");
                    }
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
                    if(sourceType == ArticleSourceType.ORIGINAL){
                        sourceData = "";
                    }
                    versionModel.setSourceType(sourceType);
                    versionModel.setSourceData(sourceData);

                    if(StringUtils.isEmpty(detailModel.getCover())){
                        JSONObject coverObj = StringUtils.getEditorContentCover(content);
                        if(coverObj != null){
                            int result = infoDao.updateMetas(id,null,coverObj.getString("src"));
                            if(result != 1) throw new Exception("更新封面失败");
                        }
                    }

                }else{
                    versionModel.setDescription(description == null ? detailModel.getDescription() : description);
                    versionModel.setTags(tags == null ? oldTags : tags);
                    versionModel.setSourceType(sourceType == null ? detailModel.getSourceType() : sourceType);
                    versionModel.setSourceData(sourceData == null ? detailModel.getSourceData() : sourceData);
                    ColumnInfoModel column = detailModel.getColumn();
                    versionModel.setColumnId(columnId == null ? (column != null ? column.getId() : 0) : columnId );
                }

                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.getTimestamp());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int rows = isPublish == true ? updateVersion(versionModel) : updateDraft(versionModel);
                if (rows != 1)
                    throw new Exception("新增版本失败");
                if(isPublish == true && detailModel.isPublished() == false) rows = userService.updateArticleCount(detailModel.getCreatedUserId(),1);
                if(rows != 1) throw new Exception("更新用户文章数量失败");
            }
            String draftTitle = versionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),"/article/" + id.toString(),draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.ARTICLE,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()) {
                    // 首次发布
                    operationalPublisher.publish(new ArticleEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.getTimestamp(), ip));
                }else{
                    // 文章更新
                    operationalPublisher.publish(new ArticleEvent(this, EntityAction.UPDATE,
                            detailModel.getId(), detailModel.getCreatedUserId(), userId, DateUtils.getTimestamp(), ip));
                }
                HashSet<Long> tagIds = new HashSet<>();
                if(addTags != null && addTags.size() > 0){
                    for(Long i : addTags){
                        if(!tagIds.contains(i)) tagIds.add(i);
                    }
                }
                if(delTags != null && delTags.size() > 0){
                    for(Long i : delTags){
                        if(!tagIds.contains(i)) tagIds.add(i);
                    }
                }
                if(tagIds.size() > 0){
                    operationalPublisher.publish(new TagIndexEvent(this,tagIds));
                }

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
