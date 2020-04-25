package com.itellyou.service.question.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.dao.question.QuestionVersionDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.*;
import com.itellyou.service.question.QuestionIndexService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.question.QuestionVersionService;
import com.itellyou.service.tag.TagIndexService;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class QuestionVersionServiceImpl implements QuestionVersionService {

    private final QuestionVersionDao versionDao;
    private final QuestionInfoDao infoDao;
    private final UserInfoService userService;
    private final UserBankService bankService;

    private final TagInfoService tagService;

    private final TagIndexService tagIndexService;

    private final UserDraftService draftService;
    private final QuestionSearchService searchService;
    private final QuestionIndexService indexerService;
    private final UserOperationalService operationalService;

    @Autowired
    public QuestionVersionServiceImpl(QuestionVersionDao questionVersionDao,QuestionInfoDao infoDao,UserInfoService userService,UserBankService bankService, TagInfoService tagService,TagIndexService tagIndexService, UserDraftService draftService,QuestionSearchService searchService, QuestionIndexService indexerService,UserOperationalService operationalService){
        this.versionDao = questionVersionDao;
        this.infoDao = infoDao;
        this.userService = userService;
        this.bankService = bankService;
        this.tagService = tagService;
        this.tagIndexService = tagIndexService;
        this.draftService = draftService;
        this.searchService = searchService;
        this.indexerService = indexerService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(QuestionVersionModel questionVersionModel) {
        try{
            int rows = versionDao.insert(questionVersionModel);
            if(rows != 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(questionVersionModel.getId());
            questionVersionModel.setVersion(version);
            List<TagInfoModel> tags = questionVersionModel.getTags();
            if(tags != null && tags.size() > 0){
                rows = insertTag(questionVersionModel.getId(),tags);
                if(rows != tags.size()){
                    throw new Exception("写入版本标签失败");
                }
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @Transactional
    public int update(QuestionVersionModel versionModel) {
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
            e.printStackTrace();
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
    public List<QuestionVersionModel> searchByQuestionId(Long questionId,Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return versionDao.search(null,questionId,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<QuestionVersionModel> searchByQuestionId(Long questionId){
        return searchByQuestionId(questionId,false);
    }

    @Override
    public QuestionVersionModel findById(Long id) {
        return findByQuestionIdAndId(id,null);
    }

    @Override
    public QuestionVersionModel findByQuestionIdAndId(Long id, Long questionId) {
        List<QuestionVersionModel> list = versionDao.search(id,questionId,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }


    @Override
    public int updateVersion(Long questionId, Integer version, Long ip, Long user) {
        return updateVersion(questionId,version,false,ip,user);
    }

    @Override
    public int updateVersion(Long questionId, Integer version,Boolean isPublished, Long ip, Long user) {
        return updateVersion(questionId,version,null,isPublished, DateUtils.getTimestamp(),ip,user);
    }

    @Override
    public int updateVersion(Long questionId, Integer version, Integer draft,Boolean isPublished, Long time, Long ip, Long user) {
        return infoDao.updateVersion(questionId,version,draft,isPublished,time,ip,user);
    }

    @Override
    @Transactional
    public int updateVersion(QuestionVersionModel versionModel) {
        try{
            Long versionId = versionModel.getId();
            int result = versionId == null || versionId.equals(0l) ? insert(versionModel) : update(versionModel);
            if(result != 1)
            {
                throw new Exception("更新版本失败");
            }
            result = updateVersion(versionModel.getQuestionId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            if(versionModel.getRewardAdd() > 0){
                result = bankService.update(-versionModel.getRewardAdd(), UserBankType.valueOf(versionModel.getRewardType().getValue()),versionModel.getCreatedUserId(),"提问[" + versionModel.getTitle() + "]悬赏", UserBankLogType.QUESTION_ASK,versionModel.getQuestionId().toString(),versionModel.getCreatedIp());
                if(result != 1){
                    throw new Exception("赏金扣除失败");
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long questionId, Integer version,Boolean isPublished, Long time, Long ip, Long user) {
        return updateVersion(questionId,null,version,isPublished,time,ip,user);
    }

    @Override
    public int updateDraft(Long questionId, Integer version, Long time, Long ip, Long user) {
        return updateDraft(questionId,version,null,time,ip,user);
    }

    @Override
    @Transactional
    public int updateDraft(QuestionVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateDraft(versionModel.getQuestionId(),versionModel.getVersion(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
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
    public QuestionVersionModel addVersion(Long id, Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, List<TagInfoModel> tags, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            QuestionVersionModel versionModel = new QuestionVersionModel();
            versionModel.setQuestionId(id);
            QuestionDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            if(detailModel == null)
            {
                QuestionInfoModel infoModel = searchService.findById(id);
                if(infoModel != null){
                    detailModel = new QuestionDetailModel();
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
                    versionModel.setRewardType(rewardType == null ? detailModel.getRewardType() : rewardType);
                    versionModel.setRewardValue(rewardValue == null ? detailModel.getRewardValue() : rewardValue);
                    versionModel.setDescription(description);
                    versionModel.setRewardAdd(rewardAdd == null ? 0.0 : rewardAdd);
                    versionModel.setTags(tags);

                    if(addTags != null && addTags.size() > 0){
                        int result = tagService.updateQuestionCountById(addTags,1);
                        if(result != addTags.size()) throw new Exception("更新标签提问数量失败");
                    }
                    if(delTags != null && delTags.size() > 0){
                        int result = tagService.updateQuestionCountById(delTags,-1);
                        if(result != delTags.size()) throw new Exception("更新标签提问数量失败");
                    }
                    if(StringUtils.isEmpty(detailModel.getCover())){
                        JSONObject coverObj = StringUtils.getEditorContentCover(content);
                        if(coverObj != null){
                            int result = infoDao.updateMetas(id,coverObj.getString("src"));
                            if(result != 1) throw new Exception("更新封面失败");
                        }
                    }
                }else{
                    versionModel.setDescription(description == null ? detailModel.getDescription() : description);
                    versionModel.setRewardType(rewardType == null ? detailModel.getRewardType() : rewardType);
                    versionModel.setRewardValue(rewardValue == null ? detailModel.getRewardValue() : rewardValue);
                    versionModel.setRewardAdd(rewardAdd == null ? 0.0 : rewardAdd);
                    versionModel.setTags(tags == null ? oldTags : tags);
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
                //第一次发布，更新用户的文章数量
                if(isPublish == true && detailModel.isPublished() == false){
                    int result = userService.updateQuestionCount(detailModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户问题数量失败");
                }
            }
            String url = "/question/" + id.toString();
            String draftTitle = versionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.QUESTION,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()){
                    operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.PUBLISH, EntityType.QUESTION,detailModel.getId(),detailModel.getCreatedUserId(),userId,DateUtils.getTimestamp(),ip));
                }
                detailModel = searchService.getDetail(detailModel.getId());
                indexerService.updateIndex(detailModel);
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
                if(tagIds.size() > 0) tagIndexService.updateIndex(tagIds);
            }
            return versionModel;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}
