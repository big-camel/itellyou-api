package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleCommentDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.article.ArticleCommentModel;
import com.itellyou.service.article.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class ArticleCommentSearchServiceImpl implements ArticleCommentSearchService {

    private final ArticleCommentDao commentDao;

    @Autowired
    public ArticleCommentSearchServiceImpl(ArticleCommentDao commentDao){
        this.commentDao = commentDao;
    }

    @Override
    public ArticleCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<ArticleCommentDetailModel> search(HashSet<Long> ids, Long articleId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        return commentDao.search(ids,articleId,parentId,replyId,searchUserId,userId,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<ArticleCommentDetailModel> search(Long articleId, Long parentId, Long searchUserId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,articleId,parentId,null,searchUserId,null,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, Long articleId, Long parentId, Long replyId, Long userId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,articleId,parentId,replyId,userId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long articleId, Long parentId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,articleId,parentId,null,null,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<ArticleCommentDetailModel> page(Long articleId, Long parentId, Long searchUserId, Boolean isDeleted, Integer childCount, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleCommentDetailModel> data = search(articleId,parentId,searchUserId,isDeleted,childCount,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(articleId,parentId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public ArticleCommentDetailModel getDetail(Long id, Long articleId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted) {
        List<ArticleCommentDetailModel> listComment = search(new HashSet<Long>(){{add(id);}},articleId,parentId,replyId,searchUserId,userId,isDeleted,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
