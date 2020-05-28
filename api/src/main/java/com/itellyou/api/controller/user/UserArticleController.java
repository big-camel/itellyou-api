package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleSearchService;
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
    public ResultModel draft(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<ArticleDetailModel> pageData = searchService.page(null,"draft",null,userModel.getId(),userModel.getId(),null,false,false,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(pageData);
    }
}
