package com.itellyou.service.upload;

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import com.itellyou.model.upload.UploadFileModel;
import com.itellyou.model.upload.UploadSource;

import java.util.Map;

public interface UploadVodService {
    Map<String,Object> createUploadVideo(String name, Long size, Long cateId, String template, Long userId, UploadSource source, String ip);
    RefreshUploadVideoResponse refreshUploadVideo(String videoId);
    GetPlayInfoResponse getPlayInfo(String videoId);
    UploadFileModel saveUploadVideo(String key,String name, Long size, Long userId, UploadSource source, String ip);
}
