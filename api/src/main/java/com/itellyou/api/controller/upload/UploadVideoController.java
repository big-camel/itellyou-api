package com.itellyou.api.controller.upload;

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.upload.UploadFileModel;
import com.itellyou.model.upload.UploadSource;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.upload.UploadVodService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
public class UploadVideoController {

    private final UploadVodService vodService;

    @Autowired
    public UploadVideoController(UploadVodService vodService) {
        this.vodService = vodService;
    }

    @GetMapping("/upload/video")
    public ResultModel upload(HttpServletRequest request, UserInfoModel userModel,
                              @RequestParam("filename") @NotNull String filename, @RequestParam("filesize") @NotNull Long filesize, @RequestParam(required = false, name = "type") String type)
    {
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(StringUtils.isEmpty(filename)){
            return new ResultModel(500,"请选择文件");
        }
        try {
            if(StringUtils.isEmpty(type))
                type = UploadSource.DEFAULT.getName();
            UploadSource uploadSource;
            try {
                uploadSource = UploadSource.valueOf(type.toUpperCase());
            }catch (Exception e){
                uploadSource = UploadSource.DEFAULT;
            }
            Map<String,Object> response = vodService.createUploadVideo(filename,filesize,1000084160l,"e80b1dea8dce91ac76aa51e018ff5f65",userModel.getId(), uploadSource, IPUtils.getClientIp(request));
            if(response == null) return new ResultModel(0,"上传视频失败");
            return new ResultModel(response);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @GetMapping("/upload/video/query")
    public ResultModel query(@RequestParam("video_id") @NotNull String videoId, UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        GetPlayInfoResponse response = vodService.getPlayInfo(videoId);
        if(response == null) return new ResultModel(0,"获取视频播放信息失败");
        Map<String,Object> data = new HashMap<>();
        GetPlayInfoResponse.VideoBase videoBase = response.getVideoBase();
        data.put("video_id",videoBase.getVideoId());
        data.put("cover_url",videoBase.getCoverURL());
        data.put("title",videoBase.getTitle());
        List<Map<String,Object>> playList = new ArrayList<>();
        for (GetPlayInfoResponse.PlayInfo playInfo : response.getPlayInfoList()){
            Map<String,Object> playData = new HashMap<>();
            playData.put("url",playInfo.getPlayURL());
            playData.put("format",playInfo.getFormat());
            playData.put("width",playInfo.getWidth());
            playData.put("height",playInfo.getHeight());
            playData.put("size",playInfo.getSize());
            playList.add(playData);
        }
        data.put("play_list",playList);
        return new ResultModel(data);
    }

    @PostMapping("/upload/video/save")
    public ResultModel save(HttpServletRequest request, UserInfoModel userModel, @RequestParam("key") @NotNull String key, @RequestParam("filename") @NotNull String filename, @RequestParam("filesize") @NotNull Long filesize, @RequestParam(required = false, name = "type") String type){
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(StringUtils.isEmpty(type))
            type = UploadSource.DEFAULT.getName();
        UploadSource uploadSource;
        try {
            uploadSource = UploadSource.valueOf(type.toUpperCase());
        }catch (Exception e){
            uploadSource = UploadSource.DEFAULT;
        }
        UploadFileModel fileModel = vodService.saveUploadVideo(key,filename,filesize,userModel.getId(),uploadSource,IPUtils.getClientIp(request));
        if(fileModel == null) return new ResultModel(0,"上传视频失败");
        return new ResultModel(fileModel);
    }
}
