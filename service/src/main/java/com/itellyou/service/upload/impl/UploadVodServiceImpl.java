package com.itellyou.service.upload.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.*;
import com.itellyou.model.upload.*;
import com.itellyou.service.thirdparty.AliService;
import com.itellyou.service.upload.UploadConfigService;
import com.itellyou.service.upload.UploadFileConfigService;
import com.itellyou.service.upload.UploadFileService;
import com.itellyou.service.upload.UploadVodService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadVodServiceImpl implements UploadVodService {

    private final AliService aliService;
    private final UploadConfigService configService;
    private final UploadFileConfigService fileConfigService;
    private final UploadFileService fileService;

    public UploadVodServiceImpl(AliService aliService, UploadConfigService configService, UploadFileConfigService fileConfigService, UploadFileService fileService) {
        this.aliService = aliService;
        this.configService = configService;
        this.fileConfigService = fileConfigService;
        this.fileService = fileService;
    }

    @Override
    public Map<String,Object> createUploadVideo(String name, Long size, Long cateId, String template, Long userId, UploadSource source, String ip) {
        try{
            UploadConfigModel configModel = configService.get(UploadType.VIDEO);
            if(configModel == null) throw new Exception("错误的上传配置参数");
            if(size / 1024 > configModel.getSize()) throw new Exception("超过 " + configModel.getSize() + " kb 上传限制");

            String extname = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
            UploadFileConfigModel fileConfigModel = fileConfigService.findVideoConfigByName(extname);
            if (fileConfigModel == null) throw new Exception("不支持的文件格式 " + extname + name);
            String key = userId + "/" + source.getName() + "/" + DateUtils.format("yyyy/MM/dd") + "/" + DateUtils.getTimestamp() + "-" + UUID.randomUUID() + "." + extname;

            CreateUploadVideoRequest request = new CreateUploadVideoRequest();
            request.setTitle(name);
            request.setFileName(key);
            request.setCateId(cateId);
            request.setTemplateGroupId(template);
            DefaultAcsClient client = this.aliService.getClient("cn-shanghai");
            CreateUploadVideoResponse response = client.getAcsResponse(request);
            if(response != null){
                Map<String,Object> data = new HashMap<>();
                data.put("key",key);
                data.put("video_id",response.getVideoId());
                data.put("request_id",response.getRequestId());
                data.put("upload_address",response.getUploadAddress());
                data.put("upload_auth",response.getUploadAuth());
                return data;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RefreshUploadVideoResponse refreshUploadVideo(String videoId) {
        try {
            RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
            request.setVideoId(videoId);

            DefaultAcsClient client = this.aliService.getClient("cn-shanghai");
            return client.getAcsResponse(request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GetPlayInfoResponse getPlayInfo(String videoId) {
        try {
            GetPlayInfoRequest request = new GetPlayInfoRequest();
            request.setVideoId(videoId);
            DefaultAcsClient client = this.aliService.getClient("cn-shanghai");
            return client.getAcsResponse(request);
        } catch (Exception e) {
            //System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public UploadFileModel saveUploadVideo(String key,String name, Long size, Long userId, UploadSource source, String ip) {
        String extname = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        UploadConfigModel configModel = configService.get(UploadType.VIDEO);
        UploadFileModel fileModel = new UploadFileModel(key, name, extname, configModel.getDomain(), configModel.getBucket(), source, size, DateUtils.getTimestamp(), userId, IPUtils.toLong(ip));
        int result = fileService.insert(fileModel);
        if (result != 1) return null;
        return fileModel;
    }
}
