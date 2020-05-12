package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.service.question.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "question_answer_comment")
@Service
public class QuestionAnswerCommentSearchServiceImpl implements QuestionAnswerCommentSearchService {

    private final QuestionAnswerCommentDao commentDao;

    @Autowired
    public QuestionAnswerCommentSearchServiceImpl(QuestionAnswerCommentDao commentDao){
        this.commentDao = commentDao;
    }

    @Override
    @Cacheable
    public QuestionAnswerCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<QuestionAnswerCommentDetailModel> search(HashSet<Long> ids, Long answerId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        return commentDao.search(ids,answerId,parentId,replyId,searchUserId,userId,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<QuestionAnswerCommentDetailModel> search(Long answerId, Long parentId, Long searchUserId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,answerId,parentId,null,searchUserId,null,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, Long answerId, Long parentId, Long replyId, Long userId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,answerId,parentId,replyId,userId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long answerId, Long parentId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,answerId,parentId,null,null,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<QuestionAnswerCommentDetailModel> page(Long answerId, Long parentId, Long searchUserId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerCommentDetailModel> data = search(answerId,parentId,searchUserId,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(answerId,parentId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public QuestionAnswerCommentDetailModel getDetail(Long id, Long answerId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted) {
        List<QuestionAnswerCommentDetailModel> listComment = search(new HashSet<Long>(){{add(id);}},answerId,parentId,replyId,searchUserId,userId,isDeleted,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
