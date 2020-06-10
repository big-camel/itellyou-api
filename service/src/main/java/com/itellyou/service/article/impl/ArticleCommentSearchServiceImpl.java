package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleCommentDao;
import com.itellyou.model.article.*;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.article.ArticleCommentSearchService;
import com.itellyou.service.article.ArticleCommentVoteService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "article_comment")
@Service
public class ArticleCommentSearchServiceImpl implements ArticleCommentSearchService {

    private final ArticleCommentDao commentDao;
    private final UserSearchService userSearchService;
    private final ArticleSearchService articleSearchService;
    private final ArticleCommentVoteService commentVoteService;

    @Autowired
    public ArticleCommentSearchServiceImpl(ArticleCommentDao commentDao, UserSearchService userSearchService, ArticleSearchService articleSearchService, ArticleCommentVoteService commentVoteService){
        this.commentDao = commentDao;
        this.userSearchService = userSearchService;
        this.articleSearchService = articleSearchService;
        this.commentVoteService = commentVoteService;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public ArticleCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<ArticleCommentDetailModel> search(HashSet<Long> ids, Long articleId, HashSet<Long> parentIds, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleCommentModel> infoModels = RedisUtils.fetchByCache("article_comment",ArticleCommentModel.class,ids,(HashSet<Long> fetchIds) ->
                commentDao.search(fetchIds,articleId,parentIds,replyId,userId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
        );

        List<ArticleCommentDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        HashSet<Long> replyIds = new LinkedHashSet<>();
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchParentIds = new LinkedHashSet<>();
        HashSet<Long> articleIds = new LinkedHashSet<>();
        for (ArticleCommentModel infoModel : infoModels) {
            if(infoModel.isDeleted()) {
                infoModel.setContent("评论已删除");
                infoModel.setHtml("评论已删除");
            }
            ArticleCommentDetailModel detailModel = new ArticleCommentDetailModel(infoModel);
            fetchIds.add(infoModel.getId());
            // 获取回复信息
            if(hasReply && infoModel.getReplyId() != null && !replyIds.contains(infoModel.getReplyId())){
                replyIds.add(infoModel.getReplyId());
            }
            // 获取子评论
            if(childCount != null && childCount > 0 && infoModel.getCommentCount() > 0 && !fetchParentIds.contains(infoModel.getId())){
                fetchParentIds.add(infoModel.getId());
            }
            // 设置权限
            if(searchUserId != null){
                detailModel.setAllowDelete(!infoModel.isDeleted() && searchUserId.equals(infoModel.getCreatedUserId()));
                detailModel.setAllowSupport(!searchUserId.equals(infoModel.getCreatedUserId()));
                detailModel.setAllowOppose(!searchUserId.equals(infoModel.getCreatedUserId()));
                detailModel.setAllowReply(!searchUserId.equals(infoModel.getCreatedUserId()));
            }
            // 设置是否是作者
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());

            detailModels.add(detailModel);
        }
        // 一次查出需要的回复
        List<ArticleCommentDetailModel> replyCommentDetails = new LinkedList<>();
        if(hasReply && replyIds.size() > 0){
            replyCommentDetails = search(replyIds,articleId,null,null,searchUserId,null,null,null,false,null,null,null,null,null,null,null,null,null,null,null,null);
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null);

        // 一次查出是否有关注,是否点赞
        List<ArticleCommentVoteModel> voteModels = new LinkedList<>();
        if(searchUserId != null){
            voteModels = commentVoteService.search(fetchIds,searchUserId);
        }
        // 一次查出需要的子评论
        List<ArticleCommentDetailModel> childCommentDetails =  new ArrayList<>();
        if(fetchParentIds.size() > 0){
            HashSet<Long> childIds = new LinkedHashSet<>();
            List<ArticleCommentModel> childModes = RedisUtils.fetchByCache("article_comment",ArticleCommentModel.class,ids,(HashSet<Long> childFetchIds) ->
                    commentDao.searchChild(childFetchIds,null,fetchParentIds,null,null,null,childCount,null,null,null,null,null,null,null,null,null,order)
            );
            for (ArticleCommentModel childModel : childModes){
                childIds.add(childModel.getId());
            }
            if(childIds.size() > 0) {
                childCommentDetails = search(childIds, null, null, null, searchUserId, null, null, null, true, null, null, null, null, null, null, null, null, null, null, null, null);
            }
        }
        // 一次查出文章信息
        List<ArticleDetailModel> articleDetailModels = articleSearchService.search(articleIds,null,null,null,searchUserId,null,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (ArticleCommentDetailModel detailModel : detailModels){
            // 设置回复
            if(hasReply && detailModel.getReplyId() != null){
                for (ArticleCommentDetailModel replyDetailModel : replyCommentDetails){
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
                for (ArticleCommentDetailModel childDetailModel : childCommentDetails){
                    if(childDetailModel.getParentId().equals(detailModel.getId())){
                        detailModel.getChild().add(childDetailModel);
                    }
                }
            }
            // 获取是否点赞
            for(ArticleCommentVoteModel voteModel : voteModels){
                if(voteModel.getCommentId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                    break;
                }
            }
            // 设置是否是作者
            for(ArticleDetailModel articleDetailModel : articleDetailModels){
                if(articleDetailModel.getId().equals(detailModel.getArticleId())){
                    detailModel.setUseAuthor(articleDetailModel.getCreatedUserId().equals(detailModel.getCreatedUserId()));
                    detailModel.setArticle(articleDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public List<ArticleCommentDetailModel> search(Long articleId, HashSet<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,articleId,parentIds,null,searchUserId,null,isDeleted,childCount,hasReply,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, Long articleId, HashSet<Long> parentIds, Long replyId, Long userId, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,articleId,parentIds,replyId,userId,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long articleId, HashSet<Long> parentIds, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,articleId,parentIds,null,null,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<ArticleCommentDetailModel> page(Long articleId, HashSet<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComments, Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleCommentDetailModel> data = search(articleId,parentIds,searchUserId,isDeleted,childCount,hasReply,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(articleId,parentIds,isDeleted,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public ArticleCommentDetailModel getDetail(Long id, Long articleId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted,Boolean hasReply) {
        List<ArticleCommentDetailModel> listComment = search(
                id != null ? new HashSet<Long>(){{add(id);}} : null,articleId,
                parentId != null ? new HashSet<Long>(){{ add(parentId); }} : null,
                replyId,searchUserId,userId,isDeleted,null,hasReply,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
