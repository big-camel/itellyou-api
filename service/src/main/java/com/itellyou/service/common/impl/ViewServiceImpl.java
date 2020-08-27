package com.itellyou.service.common.impl;

import com.itellyou.dao.common.ViewInfoDao;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.common.ViewInfoModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.common.ViewService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ViewServiceImpl implements ViewService {

    private final ViewInfoDao viewDao;
    private final QuestionSearchService questionSearchService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ArticleSingleService articleSearchService;
    private final SoftwareSingleService softwareSingleService;
    private final TagSingleService tagSingleService;

    @Autowired
    public ViewServiceImpl(ViewInfoDao viewDao, QuestionSearchService questionSearchService, QuestionAnswerSearchService answerSearchService, ArticleSingleService articleSearchService, SoftwareSingleService softwareSingleService, TagSingleService tagSingleService){
        this.viewDao = viewDao;
        this.questionSearchService = questionSearchService;
        this.answerSearchService = answerSearchService;
        this.articleSearchService = articleSearchService;
        this.softwareSingleService = softwareSingleService;
        this.tagSingleService = tagSingleService;
    }

    @Override
    public int insert(ViewInfoModel viewModel) {
        return viewDao.insert(viewModel);
    }

    @Override
    public List<ViewInfoModel> search(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return viewDao.search(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<ViewInfoModel> search(Long userId, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,userId,null,null,null,null,null,null,null,order,offset,limit);
    }

    @Override
    public List<ViewInfoModel> search(Long userId, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("updated_time","desc");
        return search(userId,order,offset,limit);
    }

    @Override
    public int count(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip) {
        return viewDao.count(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ViewInfoModel> page(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ViewInfoModel> data = search(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public int update(ViewInfoModel viewModel) {
        return viewDao.update(viewModel);
    }

    @Override
    public long insertOrUpdate(Long userId, EntityType dataType, Long dataKey, Long ip,String os,String browser) throws Exception {
        Map<String, String> order = new HashMap<>();
        order.put("updated_time","desc");

        List<ViewInfoModel> data = null;
        if(userId != null && userId > 0){
            data = search(null,userId,dataType,dataKey,null,null,null,null,null,order,0,1);
        }else{
            userId = 0l;
            data = search(null,0l,dataType,dataKey,null,null,null,null,ip,order,0,1);
        }
        String title = "无标题";
        ViewInfoModel view = data != null && data.size() > 0 ? data.get(0) : null;
        switch (dataType){
            case QUESTION:
                QuestionDetailModel questionDetailModel = questionSearchService.getDetail(dataKey);
                if(questionDetailModel != null) title = questionDetailModel.getTitle();
                break;
            case ANSWER:
                QuestionAnswerDetailModel answerDetailModel = answerSearchService.getDetail(dataKey);
                if(answerDetailModel != null) title = answerDetailModel.getQuestion().getTitle();
                break;
            case ARTICLE:
                ArticleInfoModel articleDetailModel = articleSearchService.findById(dataKey);
                if(articleDetailModel != null) title = articleDetailModel.getTitle();
                break;
            case SOFTWARE:
                SoftwareInfoModel softwareInfoModel = softwareSingleService.findById(dataKey);
                if(softwareInfoModel != null) title = softwareInfoModel.getName();
                break;
            case TAG:
                TagInfoModel tagInfoModel = tagSingleService.findById(dataKey);
                if(tagInfoModel != null) title = tagInfoModel.getName();
                break;
        }
        Long prevTime = view == null ? 0l : view.getUpdatedTime();
        if(view == null){
            view = new ViewInfoModel(title,os,browser,dataType,dataKey,userId,ip);
            int result = this.insert(view);
            if(result != 1) {
                throw new Exception("写入浏览记录失败");
            }
        }else{
            view.setTitle(title);
            view.setOs(os);
            view.setBrowser(browser);
            view.setUpdatedIp(ip);
            view.setUpdatedUserId(userId);
            view.setUpdatedTime(DateUtils.getTimestamp());
            int result = this.update(view);
            if(result != 1) {
                throw new Exception("更新浏览记录失败");
            }
        }
        return prevTime;
    }
}
