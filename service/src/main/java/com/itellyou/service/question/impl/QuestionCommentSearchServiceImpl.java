package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionCommentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.*;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.question.QuestionCommentSearchService;
import com.itellyou.service.question.QuestionCommentVoteService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.QUESTION_COMMENT_KEY)
@Service
public class QuestionCommentSearchServiceImpl implements QuestionCommentSearchService {

    private final QuestionCommentDao commentDao;
    private final UserSearchService userSearchService;
    private final QuestionSearchService questionSearchService;
    private final QuestionCommentVoteService commentVoteService;

    @Autowired
    public QuestionCommentSearchServiceImpl(QuestionCommentDao commentDao, UserSearchService userSearchService, QuestionSearchService questionSearchService, QuestionCommentVoteService commentVoteService){
        this.commentDao = commentDao;
        this.userSearchService = userSearchService;
        this.questionSearchService = questionSearchService;
        this.commentVoteService = commentVoteService;
    }

    @Override
    public QuestionCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<QuestionCommentDetailModel> search(Collection<Long> ids, Long questionId,Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {

        List<QuestionCommentModel> infoModels = RedisUtils.fetch(CacheKeys.QUESTION_COMMENT_KEY,QuestionCommentModel.class,ids,(Collection<Long> fetchIds) ->
                commentDao.search(fetchIds,questionId,parentIds,replyId,userId,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
        );
        List<QuestionCommentDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        Collection<Long> fetchIds = new LinkedHashSet<>();
        Collection<Long> replyIds = new LinkedHashSet<>();
        Collection<Long> authorIds = new LinkedHashSet<>();
        Collection<Long> fetchParentIds = new LinkedHashSet<>();
        Collection<Long> questionIds = new LinkedHashSet<>();
        for (QuestionCommentModel infoModel : infoModels) {
            if(infoModel.isDeleted()) {
                infoModel.setContent("评论已删除");
                infoModel.setHtml("评论已删除");
            }
            fetchIds.add(infoModel.getId());
            QuestionCommentDetailModel detailModel = new QuestionCommentDetailModel(infoModel);
            // 获取回复信息
            if(hasReply && infoModel.getReplyId() != null && !replyIds.contains(infoModel.getReplyId())){
                replyIds.add(infoModel.getReplyId());
            }
            // 获取子评论
            if(childCount != null && childCount > 0 && !fetchParentIds.contains(infoModel.getId())){
                fetchParentIds.add(infoModel.getId());
            }
            // 设置权限
            if(searchUserId != null){
                detailModel.setAllowDelete(!infoModel.isDeleted() && searchUserId.equals(infoModel.getCreatedUserId()));
                detailModel.setAllowSupport(!searchUserId.equals(infoModel.getCreatedUserId()));
                detailModel.setAllowOppose(!searchUserId.equals(infoModel.getCreatedUserId()));
                detailModel.setAllowReply(!searchUserId.equals(infoModel.getCreatedUserId()));
            }
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());
            // 设置是否是作者
            if(infoModel.getQuestionId() != null && !questionIds.contains(infoModel.getQuestionId()))
                questionIds.add(infoModel.getQuestionId());
            detailModels.add(detailModel);
        }
        // 一次查出需要的回复
        List<QuestionCommentDetailModel> replyCommentDetails = new LinkedList<>();
        if(hasReply && replyIds.size() > 0){
            replyCommentDetails = search(replyIds,questionId,null,null,null,null,null,null,false,null,null,null,null,null,null,null,null,null,null,null,null);
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的子评论
        List<QuestionCommentDetailModel> childCommentDetails = new LinkedList<>();
        if(fetchParentIds.size() > 0){
            Collection<Long> childIds = new LinkedHashSet<>();
            List<QuestionCommentModel> childModes = RedisUtils.fetch(CacheKeys.QUESTION_COMMENT_KEY,QuestionCommentModel.class,ids,(Collection<Long> childFetchIds) ->
                    commentDao.searchChild(childFetchIds,null,fetchParentIds,null,null,null,childCount,null,null,null,null,null,null,null,null,null,order)
            );
            for (QuestionCommentModel childModel : childModes){
                childIds.add(childModel.getId());
            }
            if(childIds.size() > 0) {
                childCommentDetails = search(childIds, null, null, null, searchUserId, null, null, null, true, null, null, null, null, null, null, null, null, null, null, null, null);
            }
        }
        // 一次查出是否有关注,是否点赞
        List<QuestionCommentVoteModel> voteModels = new LinkedList<>();
        if(searchUserId != null){
            voteModels = commentVoteService.search(fetchIds,searchUserId);
        }
        // 一次查出文章信息
        List<QuestionDetailModel> questionDetailModels = questionSearchService.search(questionIds,null,null,searchUserId,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (QuestionCommentDetailModel detailModel : detailModels){
            // 设置回复
            if(hasReply && detailModel.getReplyId() != null){
                for (QuestionCommentDetailModel replyDetailModel : replyCommentDetails){
                    if(detailModel.getReplyId().equals(replyDetailModel.getId())){
                        detailModel.setReply(replyDetailModel);
                        break;
                    }
                }
            }
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getId())){
                    detailModel.setAuthor(userDetailModel);
                    break;
                }
            }
            // 设置子评论
            if(childCount != null && childCount > 0){
                detailModel.setChild(new LinkedList<>());
                for (QuestionCommentDetailModel childDetailModel : childCommentDetails){
                    if(childDetailModel.getParentId().equals(detailModel.getId())){
                        detailModel.getChild().add(childDetailModel);
                    }
                }
            }
            // 获取是否点赞
            for(QuestionCommentVoteModel voteModel : voteModels){
                if(voteModel.getCommentId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                    break;
                }
            }
            // 设置是否是作者
            for(QuestionDetailModel questionDetailModel : questionDetailModels){
                if(questionDetailModel.getId().equals(detailModel.getQuestionId())){
                    detailModel.setUseAuthor(questionDetailModel.getCreatedUserId().equals(detailModel.getCreatedUserId()));
                    detailModel.setQuestion(questionDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public List<QuestionCommentDetailModel> search(Long questionId, Collection<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,questionId,parentIds,null,searchUserId,null,isDeleted,childCount,hasReply,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(Collection<Long> ids, Long questionId, Collection<Long> parentIds, Long replyId, Long userId, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,questionId,parentIds,replyId,userId,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long questionId, Collection<Long> parentIds, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,questionId,parentIds,null,null,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<QuestionCommentDetailModel> page(Long questionId, Collection<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionCommentDetailModel> data = search(questionId,parentIds,searchUserId,isDeleted,childCount,hasReply,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(questionId,parentIds,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public QuestionCommentDetailModel getDetail(Long id, Long questionId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted,Boolean hasReply) {
        List<QuestionCommentDetailModel> listComment = search(id != null ? new HashSet<Long>(){{add(id);}} : null,questionId,parentId != null ? new HashSet<Long>(){{ add(parentId);}} : null,replyId,searchUserId,userId,isDeleted,null,hasReply,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
