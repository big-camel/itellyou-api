package com.itellyou.api.controller.article;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.view.ViewInfoModel;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.BrowserUtils;
import com.itellyou.util.DateUtils;
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
@RequestMapping("/article")
public class ArticleController {

    private final ArticleSearchService searchService;
    private final ArticleInfoService infoService;
    private final UserDraftService draftService;

    @Autowired
    public ArticleController(ArticleSearchService searchService,ArticleInfoService infoService, UserDraftService draftService){
        this.searchService = searchService;
        this.infoService = infoService;
        this.draftService = draftService;
    }

    @GetMapping("/{id:\\d+}/view")
    public Result view(HttpServletRequest request, UserInfoModel userInfo,@PathVariable Long id){
        ArticleDetailModel detailModel = searchService.getDetail(id);
        if(detailModel == null){
            return new Result(404,"错误的编号");
        }
        String ip = IPUtils.getClientIp(request);
        Long longIp = IPUtils.toLong(ip);
        String os = OsUtils.getClientOs(request);
        String browser = BrowserUtils.getClientBrowser(request);
        if(browser == "Robot/Spider"){
            return new Result(0,"Robot/Spider Error");
        }
        Long userId = userInfo == null ? 0 : userInfo.getId();
        int result = infoService.updateView(userId,id,longIp,os,browser);
        if(result == 1) return new Result();
        return new Result(0,"更新失败");
    }

    @GetMapping("/{id:\\d+}/user_draft")
    public Result find(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new Result(401,"未登陆");
        }

        ArticleInfoModel infoModel = searchService.findById(id);
        if(infoModel != null && !infoModel.isDisabled()){
            boolean result = draftService.exists(userModel.getId(), EntityType.ARTICLE,infoModel.getId());
            Map<String,Object> userAnswerMap = new HashMap<>();
            userAnswerMap.put("published",infoModel.isPublished());
            userAnswerMap.put("deleted",infoModel.isDeleted());
            userAnswerMap.put("id",infoModel.getId());
            userAnswerMap.put("draft",result);
            return new Result(userAnswerMap);
        }
        return new Result(404,"Not find");
    }

    @DeleteMapping("/{id:\\d+}/user_draft")
    public Result deleteDraft(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        int result = draftService.delete(userModel.getId(), EntityType.ARTICLE,id);
        if(result != 1) return new Result(0,"删除失败");
        return new Result();
    }

    @PostMapping(value = { "/{id:\\d+}/{type:support}","/{id:\\d+}/{type:oppose}"})
    public Result vote(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id, @PathVariable String type){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        Map<String,Object> data = infoService.updateVote(VoteType.valueOf(type.toUpperCase()),id,userModel.getId(), IPUtils.getClientIp(request));
        if(data == null){
            return new Result(0,"更新失败");
        }
        return new Result(data);
    }

    @DeleteMapping("{id:\\d+}")
    public Result delete(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null) return new Result(401,"未登陆");
        int result = infoService.updateDeleted(true,id,userModel.getId());
        if(result == 1) {
            return new Result();
        }
        return new Result(0,"删除失败");
    }
}
