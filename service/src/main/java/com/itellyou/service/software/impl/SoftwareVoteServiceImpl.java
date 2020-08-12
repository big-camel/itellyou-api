package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareVoteDao;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.software.SoftwareVoteModel;
import com.itellyou.model.event.SoftwareEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.software.SoftwareInfoService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.service.software.impl.SoftwareVoteSearchServiceImpl;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.Map;

@CacheConfig(cacheNames = "software_vote")
@Service
public class SoftwareVoteServiceImpl implements VoteService<SoftwareVoteModel> {

    private Logger logger = LoggerFactory.getLogger(SoftwareVoteServiceImpl.class);

    private final SoftwareVoteDao voteDao;
    private final SoftwareInfoService infoService;
    private final SoftwareSingleService searchService;
    private final VoteSearchService<SoftwareVoteModel> voteSearchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public SoftwareVoteServiceImpl(SoftwareVoteDao voteDao, SoftwareInfoService infoService, SoftwareSingleService searchService, SoftwareVoteSearchServiceImpl voteSearchService, OperationalPublisher operationalPublisher){
        this.voteDao = voteDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.voteSearchService = voteSearchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(SoftwareVoteModel voteModel) {
        RedisUtils.clear("software_vote_" + voteModel.getCreatedUserId());
        return voteDao.insert(voteModel);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#softwareId).concat('-').concat(#userId)")
    public int deleteByTargetIdAndUserId(Long softwareId, Long userId) {
        RedisUtils.clear("software_vote_" + userId);
        return voteDao.deleteBySoftwareIdAndUserId(softwareId,userId);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#id).concat('-').concat(#userId)")
    @Transactional
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            Map<String,Object> data = new HashMap<>();
            SoftwareVoteModel voteModel = voteSearchService.findByTargetIdAndUserId(id,userId);

            if(voteModel != null){
                int result = deleteByTargetIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = infoService.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                int result = insert(new SoftwareVoteModel(id,type, DateUtils.getTimestamp(),userId, ip));
                if(result != 1) throw new Exception("写入Vote失败");
                result = infoService.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            SoftwareInfoModel infoModel = searchService.findById(id);
            if(infoModel == null) throw new Exception("获取文章失败");
            if(infoModel.getCreatedUserId().equals(userId)) throw new Exception("不能自己给自己点赞");
            data.put("id",infoModel.getId());
            data.put("support",infoModel.getSupport());
            data.put("oppose",infoModel.getOppose());
            if(voteModel != null){
                operationalPublisher.publish(new SoftwareEvent(this,
                        voteModel.getType().equals(VoteType.SUPPORT) ? EntityAction.UNLIKE : EntityAction.UNDISLIKE,
                        infoModel.getId(),infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                operationalPublisher.publish(new SoftwareEvent(this,
                        type.equals(VoteType.SUPPORT) ? EntityAction.LIKE : EntityAction.DISLIKE,
                        infoModel.getId(),infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            RedisUtils.clear("software_vote_" + userId);
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
