package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionCommentDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.question.QuestionCommentModel;
import com.itellyou.service.question.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class QuestionCommentSearchServiceImpl implements QuestionCommentSearchService {

    private final QuestionCommentDao commentDao;

    @Autowired
    public QuestionCommentSearchServiceImpl(QuestionCommentDao commentDao){
        this.commentDao = commentDao;
    }

    @Override
    public QuestionCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<QuestionCommentDetailModel> search(HashSet<Long> ids, Long questionId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        return commentDao.search(ids,questionId,parentId,replyId,searchUserId,userId,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<QuestionCommentDetailModel> search(Long questionId, Long parentId, Long searchUserId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,questionId,parentId,null,searchUserId,null,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, Long questionId, Long parentId, Long replyId, Long userId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,questionId,parentId,replyId,userId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long questionId, Long parentId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,questionId,parentId,null,null,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<QuestionCommentDetailModel> page(Long questionId, Long parentId, Long searchUserId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionCommentDetailModel> data = search(questionId,parentId,searchUserId,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(questionId,parentId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public QuestionCommentDetailModel getDetail(Long id, Long questionId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted) {
        List<QuestionCommentDetailModel> listComment = search(new HashSet<Long>(){{add(id);}},questionId,parentId,replyId,searchUserId,userId,isDeleted,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
