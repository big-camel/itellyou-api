package com.itellyou.api.controller.user;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.user.UserDraftService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/article")
public class UserArticleController {

    private final ArticleSearchService searchService;

    public UserArticleController(ArticleSearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping("")
    public Result draft(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new Result(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<ArticleDetailModel> pageData = searchService.page(null,null,null,userModel.getId(),userModel.getId(),null,false,false,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
        return new Result(pageData);
    }
}
