package com.itellyou.service.collab;

import com.itellyou.model.collab.CollabInfoModel;

public interface CollabInfoService {

    int insert(CollabInfoModel collabInfoModel);

    CollabInfoModel findById(Long id);

    CollabInfoModel findByToken(String token);

    CollabInfoModel createDefault(String key , Long userId,String clientIp);

    CollabInfoModel createByHost(String host,String key,Long userId,String clientIp);

    CollabInfoModel createByHostKey(String hostKey,String key,Long userId,String clientIp);
}
