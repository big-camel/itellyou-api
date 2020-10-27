package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareCommentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareCommentDetailModel;
import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.software.SoftwareCommentVoteModel;
import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.software.SoftwareCommentSearchService;
import com.itellyou.service.software.SoftwareCommentVoteService;
import com.itellyou.service.software.SoftwareSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_COMMENT_KEY)
@Service
public class SoftwareCommentSearchServiceImpl implements SoftwareCommentSearchService {

    private final SoftwareCommentDao commentDao;
    private final UserSearchService userSearchService;
    private final SoftwareSearchService softwareSearchService;
    private final SoftwareCommentVoteService commentVoteService;

    @Autowired
    public SoftwareCommentSearchServiceImpl(SoftwareCommentDao commentDao, UserSearchService userSearchService, SoftwareSearchService softwareSearchService, SoftwareCommentVoteService commentVoteService){
        this.commentDao = commentDao;
        this.userSearchService = userSearchService;
        this.softwareSearchService = softwareSearchService;
        this.commentVoteService = commentVoteService;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public SoftwareCommentModel findById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<SoftwareCommentDetailModel> search(Collection<Long> ids, Long softwareId, Collection<Long> parentIds, Long replyId, Long searchUserId, Long userId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SoftwareCommentModel> infoModels = RedisUtils.fetch(CacheKeys.SOFTWARE_COMMENT_KEY,SoftwareCommentModel.class,ids,(Collection<Long> fetchIds) ->
                commentDao.search(fetchIds,softwareId,parentIds,replyId,userId,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
        );

        List<SoftwareCommentDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        Collection<Long> fetchIds = new LinkedHashSet<>();
        Collection<Long> replyIds = new LinkedHashSet<>();
        Collection<Long> authorIds = new LinkedHashSet<>();
        Collection<Long> fetchParentIds = new LinkedHashSet<>();
        Collection<Long> softwareIds = new LinkedHashSet<>();
        for (SoftwareCommentModel infoModel : infoModels) {
            if(infoModel.isDeleted()) {
                infoModel.setContent("评论已删除");
                infoModel.setHtml("评论已删除");
            }
            SoftwareCommentDetailModel detailModel = new SoftwareCommentDetailModel(infoModel);
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
        List<SoftwareCommentDetailModel> replyCommentDetails = new LinkedList<>();
        if(hasReply && replyIds.size() > 0){
            replyCommentDetails = search(replyIds,softwareId,null,null,searchUserId,null,null,null,false,null,null,null,null,null,null,null,null,null,null,null,null);
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null);

        // 一次查出是否有关注,是否点赞
        List<SoftwareCommentVoteModel> voteModels = new LinkedList<>();
        if(searchUserId != null){
            voteModels = commentVoteService.search(fetchIds,searchUserId);
        }
        // 一次查出需要的子评论
        List<SoftwareCommentDetailModel> childCommentDetails =  new ArrayList<>();
        if(fetchParentIds.size() > 0){
            Collection<Long> childIds = new LinkedHashSet<>();
            List<SoftwareCommentModel> childModes = RedisUtils.fetch(CacheKeys.SOFTWARE_COMMENT_KEY,SoftwareCommentModel.class,ids,(Collection<Long> childFetchIds) ->
                    commentDao.searchChild(childFetchIds,null,fetchParentIds,null,null,null,childCount,null,null,null,null,null,null,null,null,null,order)
            );
            for (SoftwareCommentModel childModel : childModes){
                childIds.add(childModel.getId());
            }
            if(childIds.size() > 0) {
                childCommentDetails = search(childIds, null, null, null, searchUserId, null, null, null, true, null, null, null, null, null, null, null, null, null, null, null, null);
            }
        }
        // 一次查出文章信息
        List<SoftwareDetailModel> softwareDetailModels = softwareSearchService.search(softwareIds,null,null,null,searchUserId,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (SoftwareCommentDetailModel detailModel : detailModels){
            // 设置回复
            if(hasReply && detailModel.getReplyId() != null){
                for (SoftwareCommentDetailModel replyDetailModel : replyCommentDetails){
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
                for (SoftwareCommentDetailModel childDetailModel : childCommentDetails){
                    if(childDetailModel.getParentId().equals(detailModel.getId())){
                        detailModel.getChild().add(childDetailModel);
                    }
                }
            }
            // 获取是否点赞
            for(SoftwareCommentVoteModel voteModel : voteModels){
                if(voteModel.getCommentId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                    break;
                }
            }
            // 设置是否是作者
            for(SoftwareDetailModel softwareDetailModel : softwareDetailModels){
                if(softwareDetailModel.getId().equals(detailModel.getSoftwareId())){
                    detailModel.setUseAuthor(softwareDetailModel.getCreatedUserId().equals(detailModel.getCreatedUserId()));
                    detailModel.setSoftware(softwareDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public List<SoftwareCommentDetailModel> search(Long softwareId, Collection<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,softwareId,parentIds,null,searchUserId,null,isDeleted,childCount,hasReply,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public int count(Collection<Long> ids, Long softwareId, Collection<Long> parentIds, Long replyId, Long userId, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        return commentDao.count(ids,softwareId,parentIds,replyId,userId,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public int count(Long softwareId, Collection<Long> parentIds, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,softwareId,parentIds,null,null,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public PageModel<SoftwareCommentDetailModel> page(Long softwareId, Collection<Long> parentIds, Long searchUserId, Boolean isDeleted, Integer childCount,Boolean hasReply, Integer minComment, Integer maxComment, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<SoftwareCommentDetailModel> data = search(softwareId,parentIds,searchUserId,isDeleted,childCount,hasReply,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,order,offset,limit);
        Integer total = count(softwareId,parentIds,isDeleted,minComment,maxComment,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public SoftwareCommentDetailModel getDetail(Long id, Long softwareId, Long parentId, Long replyId, Long searchUserId, Long userId, Boolean isDeleted,Boolean hasReply) {
        List<SoftwareCommentDetailModel> listComment = search(
                id != null ? new HashSet<Long>(){{add(id);}} : null,softwareId,
                parentId != null ? new HashSet<Long>(){{ add(parentId); }} : null,
                replyId,searchUserId,userId,isDeleted,null,hasReply,null,null,null,null,null,null,null,null,null,null,0,1);
        return listComment != null && listComment.size() > 0 ? listComment.get(0) : null;
    }
}
