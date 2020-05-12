package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.*;
import com.itellyou.model.user.*;
import com.itellyou.service.question.*;
import com.itellyou.service.user.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "question_answer")
public class QuestionAnswerSearchServiceImpl implements QuestionAnswerSearchService {

    private final QuestionAnswerDao answerDao;
    private final UserSearchService userSearchService;

    @Autowired
    public QuestionAnswerSearchServiceImpl(QuestionAnswerDao answerDao,UserSearchService userSearchService){
        this.answerDao = answerDao;
        this.userSearchService = userSearchService;
    }

    @Override
    public QuestionAnswerModel findByQuestionIdAndUserId(Long questionId, Long userId) {
        return answerDao.findByQuestionIdAndUserId(questionId,userId);
    }

    @Override
    @Cacheable
    public QuestionAnswerModel findById(Long id) {
        return answerDao.findById(id);
    }

    @Override
    public List<QuestionAnswerDetailModel> search(HashSet<Long> ids, Long questionId, String mode, Long searchUserId, Long userId, Boolean hasContent, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return answerDao.search(ids,questionId,mode,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,ip,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);

    }

    @Override
    public int count(HashSet<Long> ids, Long questionId, String mode, Long searchUserId, Long userId,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long ip, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return answerDao.count(ids,questionId,mode,searchUserId,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public List<QuestionAnswerDetailModel> search(HashSet<Long> ids, Long questionId, String mode, Long searchUserId, Long userId, Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,questionId,mode,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionAnswerDetailModel> search(HashSet<Long> ids, Long questionId, String mode, Long searchUserId, Long userId,Boolean hasContent, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,questionId,mode,searchUserId,userId,hasContent,null,null,null,null,beginTime,endTime,order,offset,limit);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,questionId,null,searchUserId,null,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(questionId,searchUserId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long questionId, Long searchUserId,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return count(null,questionId,null,searchUserId,null,isAdopted,isDisabled,isPublished,isDeleted,null,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(questionId,searchUserId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(questionId,searchUserId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long questionId, Long searchUserId,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime) {
        return count(questionId,searchUserId,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);

    }

    @Override
    public PageModel<QuestionAnswerDetailModel> page(Long questionId, Long searchUserId,Long userId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime,Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionAnswerDetailModel> data = search(null,questionId,null,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
        Integer total = count(null,questionId,null,searchUserId,userId,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Long questionId, String mode, Long searchUserId, Long userId, Boolean hasContent,Boolean isAdopted, Boolean isDisabled,Boolean isPublished, Boolean isDeleted) {
        List<QuestionAnswerDetailModel> listQuestion = search(new HashSet<Long>(){{add(id);}},questionId,mode,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId, String mode, Long searchUserId, Long userId,Boolean hasContent) {
        List<QuestionAnswerDetailModel> listQuestion = search(new HashSet<Long>(){{add(id);}},questionId,mode,searchUserId,userId,hasContent,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Long questionId, String mode, Long searchUserId, Long userId) {
        return getDetail(id,questionId,mode,null,userId,null);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId, String mode,Boolean hasContent) {
        return getDetail(id,questionId,mode,null,null,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId, Long searchUserId, Long userId,Boolean hasContent) {
        return getDetail(id,questionId,null,searchUserId,userId,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Long questionId, Boolean hasContent) {
        return getDetail(id,questionId,null, null,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId) {
        return getDetail(id,questionId,(Boolean) null);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Boolean hasContent) {
        return getDetail(id,null,null, null,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id) {
        return getDetail(id,(Boolean) null);
    }

    @Override
    public List<Map<String,Object>> groupByUserId(Long questionId,Long searchId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<Map<String, String>> list = answerDao.groupByUserId(questionId,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);

        HashSet<Long> userHash = new HashSet<>();
        Map<Long, Integer> data = new LinkedHashMap<>();
        for (Map<String, String> map : list){
            Long userId=null;
            Integer count=null;
            for (Map.Entry<String, String> entry:map.entrySet()) {
                if(entry.getKey().equals("user_id")) userId = Long.parseLong(String.valueOf(entry.getValue()));
                if(entry.getKey().equals("count")) count = Integer.parseInt(String.valueOf(entry.getValue()));
            }
            userHash.add(userId);
            data.put(userId,count);
        }
        List<Map<String,Object>> resultData = new ArrayList<>();

        List<UserDetailModel> userDetailModels = userSearchService.search(userHash,searchId,null,null,null,null,null,null,null,null,null,null);
        for (Map.Entry<Long, Integer> entry:data.entrySet()){
            for (UserDetailModel detailModel : userDetailModels){
                if(detailModel.getId().equals(entry.getKey())){
                    Map<String, Object> map = new HashMap<>();
                    map.put("user",detailModel);
                    map.put("count",entry.getValue());
                    resultData.add(map);
                    break;
                }
            }
        }
        return resultData;
    }

    @Override
    public int groupCountByUserId(Long questionId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime) {
        return answerDao.groupCountByUserId(questionId,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime);
    }
}
