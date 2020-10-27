package com.itellyou.service.common.impl;

import com.itellyou.dao.common.ViewInfoDao;
import com.itellyou.model.common.ViewInfoModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.ViewService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ViewServiceImpl implements ViewService {

    private final ViewInfoDao viewDao;

    @Autowired
    public ViewServiceImpl(ViewInfoDao viewDao){
        this.viewDao = viewDao;
    }

    @Override
    public int insert(ViewInfoModel viewModel) {
        return viewDao.insert(viewModel);
    }

    @Override
    public int update(ViewInfoModel viewModel) {
        return viewDao.update(viewModel);
    }

    @Override
    public long insertOrUpdate(Long userId, EntityType dataType, Long dataKey,String title, Long ip,String os,String browser) throws Exception {

        //根据数据类型和数据编号、IP地址或用户编号查询浏览记录
        ViewInfoModel view = userId != null && userId > 0 ? findByUser(userId,dataType,dataKey) : findByIp(ip,dataType,dataKey);
        // 获取浏览的标题
        title = StringUtils.isEmpty(title) ? "无标题" : title;
        // 上次浏览时间
        Long prevTime = view == null ? 0l : DateUtils.getTimestamp(view.getUpdatedTime());
        String cacheKey = (userId != null && userId > 0 ? userId : ip) + "-" + dataType.getValue() + "-" + dataKey;
        if(view == null){
            // 第一次浏览，记录到数据库
            view = new ViewInfoModel(title,os,browser,dataType,dataKey,userId,ip);
            int result = this.insert(view);
            if(result != 1) {
                throw new Exception("写入浏览记录失败");
            }
        }else if(DateUtils.getTimestamp() - prevTime > 3) {//3秒内访问不更新最新访问时间
            // 更新浏览记录最新浏览时间
            view.setTitle(title);
            view.setOs(os);
            view.setBrowser(browser);
            view.setUpdatedIp(ip);
            view.setUpdatedUserId(userId);
            view.setUpdatedTime(DateUtils.toLocalDateTime());
            int result = this.update(view);
            if(result != 1) {
                throw new Exception("更新浏览记录失败");
            }
        }
        RedisUtils.set(CacheKeys.VIEW_KEY,cacheKey,view);
        return prevTime;
    }

    @Override
    public ViewInfoModel findByUser(Long userId, EntityType dataType, Long dataKey) {
        Map<String, String> order = new HashMap<>();
        order.put("updated_time","desc");
        String key = userId + "-" + dataType.getValue() + "-" + dataKey;
        ViewInfoModel infoModel = RedisUtils.get(CacheKeys.VIEW_KEY,key,ViewInfoModel.class);
        if(infoModel == null){
            List<ViewInfoModel> data = viewDao.search(null,userId,dataType,dataKey,null,null,null,null,null,order,0,1);
            infoModel = data != null && data.size() > 0 ? data.get(0) : null;
        }
        return infoModel;
    }

    @Override
    public ViewInfoModel findByIp(Long ip, EntityType dataType, Long dataKey) {
        Map<String, String> order = new HashMap<>();
        order.put("updated_time","desc");
        String key = ip + "-" + dataType.getValue() + "-" + dataKey;
        ViewInfoModel infoModel = RedisUtils.get(CacheKeys.VIEW_KEY,key,ViewInfoModel.class);
        if(infoModel == null){
        List<ViewInfoModel> data = viewDao.search(null,0l,dataType,dataKey,null,null,null,null,ip,order,0,1);
            infoModel = data != null && data.size() > 0 ? data.get(0) : null;
        }
        return infoModel;
    }
}
