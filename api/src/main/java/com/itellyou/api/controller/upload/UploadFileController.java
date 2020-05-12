package com.itellyou.api.controller.upload;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.upload.UploadFileConfigModel;
import com.itellyou.model.upload.UploadFileModel;
import com.itellyou.model.upload.UploadSource;
import com.itellyou.model.upload.UploadType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.upload.UploadFileConfigService;
import com.itellyou.service.upload.UploadOssService;
import com.itellyou.util.FileUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Validated
@RestController
public class UploadFileController {

    private final UploadOssService ossService;
    private final UploadFileConfigService fileConfigService;

    public UploadFileController(UploadOssService ossService, UploadFileConfigService fileConfigService){
        this.ossService = ossService;
        this.fileConfigService = fileConfigService;
    }

    @PostMapping("/upload/{action:image|file|doc}")
    public ResultModel upload(HttpServletRequest request, UserInfoModel userModel, @RequestParam(required = false,name = "file") MultipartFile file, @MultiRequestBody(required = false) String url , @RequestParam(required = false, name = "type") String type, @PathVariable String action){
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(StringUtils.isEmpty(url) && (file == null || file.isEmpty())){
            return new ResultModel(500,"请选择文件");
        }
        try {
            String path = null;
            if(StringUtils.isNotEmpty(url)){
                path = FileUtils.download(url);
                if(StringUtils.isEmpty(path)) return new ResultModel(0,"下载文件出错了");
                file = FileUtils.fileToMultipartFile(path);
                if(file == null) return new ResultModel(0,"下载文件出错了");
            }

            if(StringUtils.isEmpty(type))
                type = UploadSource.DEFAULT.getName();
            UploadSource uploadSource;
            UploadType uploadType;
            try {
                uploadSource = UploadSource.valueOf(type.toUpperCase());
                uploadType = UploadType.valueOf(action.toUpperCase());
            }catch (Exception e){
                uploadSource = UploadSource.DEFAULT;
                uploadType = UploadType.FILE;
            }
            String name = file.getOriginalFilename();
            if(StringUtils.isEmpty(name)) name = file.getName();
            UploadFileModel fileModel = ossService.uploadFile(uploadType,userModel.getId(),name,file.getSize(),file.getInputStream(), uploadSource, IPUtils.getClientIp(request));
            if(fileModel == null) return new ResultModel(0,"上传文件失败");
            ResultModel resultModel = new ResultModel(fileModel);
            String extname = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
            UploadFileConfigModel configModel = fileConfigService.findConfig(extname,null,null,null,null);
            if(configModel != null && (configModel.isDoc() || configModel.isImage() || configModel.isVideo())){
                resultModel = resultModel.extend("preview","/preview?key=" + fileModel.getKey());
            }
            if(StringUtils.isNotEmpty(path)){
                FileUtils.delete(path);
            }
            return resultModel;
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }
}
