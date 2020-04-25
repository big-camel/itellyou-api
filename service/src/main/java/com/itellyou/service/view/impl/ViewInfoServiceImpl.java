package com.itellyou.service.view.impl;

import com.itellyou.dao.view.ViewInfoDao;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.view.ViewInfoModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.view.ViewInfoService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ViewInfoServiceImpl implements ViewInfoService {

    private final ViewInfoDao viewDao;
    private final QuestionSearchService questionSearchService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ArticleSearchService articleSearchService;
    private final TagSearchService tagSearchService;

    @Autowired
    public ViewInfoServiceImpl(ViewInfoDao viewDao, QuestionSearchService questionSearchService, QuestionAnswerSearchService answerSearchService, ArticleSearchService articleSearchService, TagSearchService tagSearchService){
        this.viewDao = viewDao;
        this.questionSearchService = questionSearchService;
        this.answerSearchService = answerSearchService;
        this.articleSearchService = articleSearchService;
        this.tagSearchService = tagSearchService;
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
                ArticleDetailModel articleDetailModel = articleSearchService.getDetail(dataKey);
                if(articleDetailModel != null) title = articleDetailModel.getTitle();
                break;
            case TAG:
                TagInfoModel tagInfoModel = tagSearchService.findById(dataKey);
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
