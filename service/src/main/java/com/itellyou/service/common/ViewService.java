package com.itellyou.service.common;

import com.itellyou.model.common.ViewInfoModel;
import com.itellyou.model.sys.EntityType;

public interface ViewService {
    int insert(ViewInfoModel viewModel);

    int update(ViewInfoModel viewModel);

    /**
     * 记录浏览记录，如果存在，则更新浏览时间
     * @param userId 浏览用户编号
     * @param dataType 浏览的数据类型
     * @param dataKey 浏览的数据主键
     * @param title 浏览的标题
     * @param ip 浏览的ip
     * @param os 浏览的操作系统
     * @param browser 浏览的浏览器
     * @return 上一次浏览时间
     */
    long insertOrUpdate(Long userId, EntityType dataType, Long dataKey,String title, Long ip, String os, String browser) throws Exception;

    ViewInfoModel findByUser(Long userId, EntityType dataType, Long dataKey);

    ViewInfoModel findByIp(Long ip, EntityType dataType, Long dataKey);
}
