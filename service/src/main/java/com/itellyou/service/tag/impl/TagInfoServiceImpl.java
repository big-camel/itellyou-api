package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagVersionService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

@CacheConfig(cacheNames = "tag")
@Service
public class TagInfoServiceImpl implements TagInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagInfoDao tagInfoDao;
    private final TagVersionService versionService;

    @Autowired
    public TagInfoServiceImpl(TagInfoDao tagInfoDao,TagVersionService versionService){
        this.tagInfoDao = tagInfoDao;
        this.versionService = versionService;
    }

    @Override
    public int insert(TagInfoModel tagInfoModel) {
        return tagInfoDao.insert(tagInfoModel);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStarCountById(Long id, Integer step) {
        List<Long> list = new ArrayList<>();
        list.add(id);
        return updateStarCountById(list,step);
    }

    @Override
    public int updateStarCountById(List<Long> ids, Integer step) {
        if(ids == null || ids.size() < 1) return 0;
        return tagInfoDao.updateStarCountById(ids,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateArticleCountById(Long id, Integer step) {
        List<Long> list = new ArrayList<>();
        list.add(id);
        return updateArticleCountById(list,step);
    }

    @Override
    public int updateArticleCountById(List<Long> ids, Integer step) {
        if(ids == null || ids.size() < 1) return 0;
        return tagInfoDao.updateArticleCountById(ids,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateQuestionCountById(Long id, Integer step) {
        List<Long> list = new ArrayList<>();
        list.add(id);
        return updateQuestionCountById(list,step);
    }

    @Override
    public int updateQuestionCountById(List<Long> ids, Integer step) {
        if(ids == null || ids.size() < 1) return 0;
        return tagInfoDao.updateQuestionCountById(ids,step);
    }



    @Override
    @Transactional
    public Long create(Long userId,String name, String content, String html,String icon, String description, String remark, String save_type, Long ip) throws Exception {
        try{
            TagInfoModel infoModel = new TagInfoModel();
            infoModel.setName(name);
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.getTimestamp());
            infoModel.setCreatedUserId(userId);
            int resultRows = insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入标签失败");
            TagVersionModel versionModel = versionService.addVersion(infoModel.getId(),userId,content,html,icon,description,remark,1,save_type,ip,true,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

}
