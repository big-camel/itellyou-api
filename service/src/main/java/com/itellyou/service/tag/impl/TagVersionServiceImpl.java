package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.dao.tag.TagVersionDao;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.service.tag.TagVersionSearchService;
import com.itellyou.service.tag.TagVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "tag_version")
@Service
public class TagVersionServiceImpl implements TagVersionService {

    private final TagVersionDao versionDao;
    private final TagInfoDao infoDao;
    private final TagVersionSearchService searchService;

    @Autowired
    public TagVersionServiceImpl(TagVersionDao versionDao, TagInfoDao infoDao, TagVersionSearchService searchService){
        this.versionDao = versionDao;
        this.infoDao = infoDao;
        this.searchService = searchService;
    }

    @Override
    public int insert(TagVersionModel versionModel) {
        try{
            int rows = versionDao.insert(versionModel);
            if(rows != 1){
                return rows;
            }
            Integer version = searchService.findVersionById(versionModel.getId());
            versionModel.setVersion(version);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"), @CacheEvict(key = "T(String).valueOf(#versionModel.tagId).concat('-').concat(#versionModel.version)")})
    public int update(TagVersionModel versionModel) {
        try{
            int rows = versionDao.update(versionModel);
            if(rows != 1){
                return rows;
            }
            if(versionModel.getVersion() == null || versionModel.getVersion() <= 0 ){
                Integer version = searchService.findVersionById(versionModel.getId());
                versionModel.setVersion(version);
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updateVersionById(Long id, Integer version, Integer draft, Boolean isPublished, Long time, Long ip, Long userId) {
        return infoDao.updateVersionById(id,version,draft,isPublished,time,ip,userId);
    }

    @Override
    public int updateVersion(Long id, Integer version, Boolean isPublished, Long time, Long ip, Long userId) {
        return updateVersionById(id,version,null,isPublished,time,ip,userId);
    }

    @Override
    @Transactional
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"), @CacheEvict(key = "T(String).valueOf(#versionModel.tagId).concat('-').concat(#versionModel.version)")})
    public int updateVersion(TagVersionModel versionModel) {
        try{
            Long versionId = versionModel.getId();
            int result = versionId == null || versionId.equals(0l) ? insert(versionModel) : update(versionModel);
            if(result != 1)
            {
                throw new Exception("更新版本失败");
            }
            result = updateVersionById(versionModel.getTagId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long id, Integer draft, Boolean isPublished, Long time, Long ip, Long userId) {
        return updateVersionById(id,null,draft,isPublished,time,ip,userId);
    }

    @Override
    @Transactional
    public int updateDraft(TagVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }

            result = updateDraft(versionModel.getTagId(),versionModel.getVersion(),null,versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新草稿版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
