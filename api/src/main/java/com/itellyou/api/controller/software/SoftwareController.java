package com.itellyou.api.controller.software;

import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.software.SoftwareVoteModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.software.SoftwareInfoService;
import com.itellyou.service.software.SoftwareSearchService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.common.impl.VoteFactory;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.BrowserUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.OsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/software")
public class SoftwareController {

    private final SoftwareSearchService searchService;
    private final SoftwareSingleService softwareSingleService;
    private final SoftwareInfoService infoService;
    private final VoteService<SoftwareVoteModel> voteService;
    private final UserDraftService draftService;

    @Autowired
    public SoftwareController(SoftwareSearchService searchService, SoftwareSingleService softwareSingleService, SoftwareInfoService infoService, UserDraftService draftService, VoteFactory voteFactory){
        this.searchService = searchService;
        this.softwareSingleService = softwareSingleService;
        this.infoService = infoService;
        this.draftService = draftService;
        this.voteService = voteFactory.create(EntityType.SOFTWARE);
    }

    @GetMapping("/{id:\\d+}/view")
    public ResultModel view(HttpServletRequest request, UserInfoModel userInfo, @PathVariable Long id){
        SoftwareDetailModel detailModel = searchService.getDetail(id);
        if(detailModel == null){
            return new ResultModel(404,"错误的编号");
        }
        String ip = IPUtils.getClientIp(request);
        Long longIp = IPUtils.toLong(ip);
        String os = OsUtils.getClientOs(request);
        String browser = BrowserUtils.getClientBrowser(request);
        if(browser == "Robot/Spider"){
            return new ResultModel(0,"Robot/Spider Error");
        }
        Long userId = userInfo == null ? 0 : userInfo.getId();
        int result = infoService.updateView(userId,id,longIp,os,browser);
        if(result == 1) return new ResultModel();
        return new ResultModel(0,"更新失败");
    }

    @GetMapping("/{id:\\d+}/user_draft")
    public ResultModel find(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }

        SoftwareInfoModel infoModel = softwareSingleService.findById(id);
        if(infoModel != null && !infoModel.isDisabled()){
            boolean result = draftService.exists(userModel.getId(), EntityType.SOFTWARE,infoModel.getId());
            Map<String,Object> userAnswerMap = new HashMap<>();
            userAnswerMap.put("published",infoModel.isPublished());
            userAnswerMap.put("deleted",infoModel.isDeleted());
            userAnswerMap.put("id",infoModel.getId());
            userAnswerMap.put("draft",result);
            return new ResultModel(userAnswerMap);
        }
        return new ResultModel(404,"Not find");
    }

    @DeleteMapping("/{id:\\d+}/user_draft")
    public ResultModel deleteDraft(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        int result = draftService.delete(userModel.getId(), EntityType.SOFTWARE,id);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }

    @PostMapping(value = { "/{id:\\d+}/{type:support}","/{id:\\d+}/{type:oppose}"})
    public ResultModel vote(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id, @PathVariable String type){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        Map<String,Object> data = voteService.doVote(VoteType.valueOf(type.toUpperCase()),id,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
        if(data == null){
            return new ResultModel(0,"更新失败");
        }
        return new ResultModel(data);
    }

    @DeleteMapping("{id:\\d+}")
    public ResultModel delete(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        int result = infoService.updateDeleted(true,id,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
        if(result == 1) {
            return new ResultModel();
        }
        return new ResultModel(0,"删除失败");
    }
}
