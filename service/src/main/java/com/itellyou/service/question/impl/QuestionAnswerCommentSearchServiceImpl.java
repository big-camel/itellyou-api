package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.question.QuestionAnswerCommentSearchService;
import com.itellyou.service.question.QuestionAnswerCommentVoteService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_COMMENT_KEY)
@Service
public class QuestionAnswerCommentSearchServiceImpl implements QuestionAnswerCommentSearchService {

    private final QuestionAnswerCommentDao commentDao;
    private final UserSearchService userSearchService;
    private final QuestionAnswerSearchService answerSearchService;
    private final QuestionAnswerCommentVoteService commentVoteService;

    @Autowired
    public QuestionAnswerCommentSearchServiceImpl(QuestionAnswerCommentDao commentDao, UserSearchService userSearchService, QuestionAnswerSearchService answerSearchService, QuestionAnswerCommentVoteService commentVoteService){
        this.commentDao = commentDao;
        this.userSearchService = userSearchService;
        this.answerSearchService = answerSearchService;
        this.commentVoteService = commentVoteService;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionAnswerCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<QuestionAnswerCommentDetailModel> search(Collection<Long> ids, Long answerId, Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerCommentModel> infoModels = RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_COMMENT_KEY,QuestionAnswerCommentModel.class,ids,(Collection<Long> fetchIds) ->
                commentDao.search(fetchIds,answerId,parentIds,replyId,userId,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
        );
        List<QuestionAnswerCommentDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        Collection<Long> fetchIds = new LinkedHashSet<>();
        Collection<Long> replyIds = new LinkedHashSet<>();
        Collection<Long> authorIds = new LinkedHashSet<>();
        Collection<Long> fetchParentIds = new LinkedHashSet<>();
        Collection<Long> answerIds = new LinkedHashSet<>();
        for (QuestionAnswerCommentModel infoModel : infoModels) {
            if(infoModel.isDeleted()) {
                infoModel.setContent("评论已删除");
                infoModel.setHtml("评论已删除");
            }
            fetchIds.add(infoModel.getId());
            QuestionAnswerCommentDetailModel detailModel = new QuestionAnswerCommentDetailModel(infoModel);
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
            if(infoModel.getAnswerId() != null && !answerIds.contains(infoModel.getAnswerId()))
                answerIds.add(infoModel.getAnswerId());
            detailModels.add(detailModel);
        }
        // 一次查出需要的回复
        List<QuestionAnswerCommentDetailModel> replyCommentDetails = new LinkedList<>();
        if(hasReply && replyIds.size() > 0){
            replyCommentDetails = search(replyIds,answerId,null,null,null,null,null,null,false,null,null,null,null,null,null,null,null,null,null,null,null);
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的子评论
        List<QuestionAnswerCommentDetailModel> childCommentDetails = new LinkedList<>();
        if(fetchParentIds.size() > 0){
            Collection<Long> childIds = new LinkedHashSet<>();
            List<QuestionAnswerCommentModel> childModes = RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_COMMENT_KEY,QuestionAnswerCommentModel.class,ids,(Collection<Long> childFetchIds) ->
                    commentDao.searchChild(childFetchIds,null,fetchParentIds,null,null,null,childCount,null,null,null,null,null,null,null,null,null,order)
            );
            for (QuestionAnswerCommentModel childModel : childModes){
                childIds.add(childModel.getId());
            }
            if(childIds.size() > 0) {
                childCommentDetails = search(childIds, null, null, null, searchUserId, null, null, null, true, null, null, null, null, null, null, null, null, null, null, null, null);
            }
        }
        // 一次查出是否有关注,是否点赞
        List<QuestionAnswerCommentVoteModel> voteModels = new LinkedList<>();
        if(searchUserId != null){
            voteModels = commentVoteService.search(fetchIds,searchUserId);
        }
        // 一次查出回答信息
        List<QuestionAnswerDetailModel> answerDetailModels = answerSearchService.search(answerIds,null,null,searchUserId,null,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (QuestionAnswerCommentDetailModel detailModel : detailModels){
            // 设置回复
            if(hasReply && detailModel.getReplyId() != null){
                for (QuestionAnswerCommentDetailModel replyDetailModel : replyCommentDetails){
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
                for (QuestionAnswerCommentDetailModel childDetailModel : childCommentDetails){
                    if(childDetailModel.getParentId().equals(detailModel.getId())){
                        detailModel.getChild().add(childDetailModel);
                    }
                }
            }
            // 获取是否点赞
            for(QuestionAnswerCommentVoteModel voteModel : voteModels){
                if(voteModel.getCommentId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                    break;
                }
            }
            // 设置是否是作者
            for(QuestionAnswerDetailModel answerDetailModel : answerDetailModels){
                if(answerDetailModel.getId().equals(detailModel.getAnswerId())){
                    detailModel.setUseAuthor(answerDetailModel.getCreatedUserId().equals(detailModel.getCreatedUserId()));
                    detailModel.setAnswer(answerDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public List<QuestionAnswerCommentDetailModel> search(Long answerId, Collection<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,answerId,parentIds,null,searchUserId,null,isDeleted,childCount,hasReply,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(Collection<Long> ids, Long answerId,Collection<Long> parentIds, Long replyId, Long userId, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,answerId,parentIds,replyId,userId,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long answerId,Collection<Long> parentIds, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,answerId,parentIds,null,null,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<QuestionAnswerCommentDetailModel> page(Long answerId, Collection<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerCommentDetailModel> data = search(answerId,parentIds,searchUserId,isDeleted,childCount,hasReply,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(answerId,parentIds,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public QuestionAnswerCommentDetailModel getDetail(Long id, Long answerId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted,Boolean hasReply) {
        List<QuestionAnswerCommentDetailModel> listComment = search(id != null ? new HashSet<Long>(){{add(id);}} : null,answerId,parentId != null ? new HashSet<Long>(){{ add(parentId);}} : null,replyId,searchUserId,userId,isDeleted,null,hasReply,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
