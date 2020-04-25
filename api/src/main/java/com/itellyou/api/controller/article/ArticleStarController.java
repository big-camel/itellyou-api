package com.itellyou.api.controller.article;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleStarDetailModel;
import com.itellyou.model.article.ArticleStarModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.article.ArticleStarService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/article")
public class ArticleStarController {

    private final ArticleStarService starService;
    private final ArticleSearchService searchService;

    public ArticleStarController(ArticleStarService starService, ArticleSearchService searchService){
        this.starService = starService;
        this.searchService = searchService;
    }

    @GetMapping("/star")
    public Result star(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new Result(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<ArticleStarDetailModel> pageData = starService.page(null,userModel.getId(),null,null,null,order,offset,limit);
        return new Result(pageData);
    }

    @PostMapping("/star")
    public Result star(HttpServletRequest request,UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new Result(401,"未登陆");
        ArticleInfoModel infoModel = searchService.findById(id);
        if(infoModel == null) return new Result(404,"错误的id");
        if(userModel.isDisabled()) return new Result(0,"错误的用户状态");
        String clientIp = IPUtils.getClientIp(request);
        Long ip = IPUtils.toLong(clientIp);
        ArticleStarModel starModel = new ArticleStarModel(id, DateUtils.getTimestamp(),userModel.getId(),ip);
        try{
            int result = starService.insert(starModel);
            if(result != 1) throw new Exception("收藏失败");
            return new Result(infoModel.getStarCount() + 1);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @DeleteMapping("/star")
    public Result delete(UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new Result(401,"未登陆");
        if(userModel.isDisabled()) return new Result(0,"错误的用户状态");
        try{
            int result = starService.delete(id,userModel.getId());
            if(result != 1) throw new Exception("取消收藏失败");
            ArticleInfoModel infoModel = searchService.findById(id);
            return new Result(infoModel.getStarCount());
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }
}
