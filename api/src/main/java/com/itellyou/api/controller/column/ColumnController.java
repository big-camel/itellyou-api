package com.itellyou.api.controller.column;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/column")
public class ColumnController {

    private final ColumnSearchService searchService;
    private final ColumnInfoService infoService;
    private final TagSearchService tagSearchService;

    @Autowired
    public ColumnController(ColumnSearchService searchService, ColumnInfoService infoService, TagSearchService tagSearchService){
        this.searchService = searchService;
        this.infoService = infoService;
        this.tagSearchService = tagSearchService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false,name = "member_id") Long memberId, @RequestParam(required = false) String type, @RequestParam(required = false,name = "tag_id") Long tagId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Long searchUserId = userModel == null ? null : userModel.getId();
        PageModel<ColumnDetailModel> data = null;
        if(type == null) type = "";
        Map<String,String > order;
        switch (type){
            case "hot":
                order = new HashMap<>();
                order.put("article_count","desc");
                order.put("star_count","desc");
                data = searchService.page(null,null,null,memberId,searchUserId,false,true,false,tagId != null ? new ArrayList<Long>(){{ add(tagId);}} : null,null,null,null,null,null,null,null,order,offset,limit);
                break;
            default:
                order = new HashMap<>();
                order.put("created_time","desc");
                data = searchService.page(null,null,null,memberId,searchUserId,false,true,false,
                        tagId != null ? new ArrayList<Long>(){{ add(tagId);}} : null,null,null,null,null,null,null,null,order,offset,limit);
        }
        return new ResultModel(data);
    }

    @GetMapping("/detail")
    public ResultModel detail(UserInfoModel userModel, @NotNull Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        ColumnDetailModel detailModel = searchService.getDetail(id,null,searchUserId);
        if(detailModel == null || detailModel.isDeleted() || detailModel.isDisabled() ) return new ResultModel(404,"Not Find");
        return new ResultModel(detailModel);
    }

    @PostMapping("/query/name")
    public ResultModel queryName(@MultiRequestBody @NotBlank String name){
        ColumnInfoModel infoModel = searchService.findByName(name);
        if(infoModel != null){
            return new ResultModel(401,"名称不可用",name);
        }
        return new ResultModel(name);
    }

    @PutMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotBlank String name, @MultiRequestBody @NotBlank String description, @MultiRequestBody @NotNull Long[] tags) {
        if(userModel == null) return new ResultModel(401,"未登陆");
        ColumnInfoModel infoModel = searchService.findByName(name);
        if(infoModel != null) return new ResultModel(1001,"专栏名称已存在");
        if(tags.length > 0){
            int rows = tagSearchService.exists(tags);
            if(rows != tags.length){
                return new ResultModel(0,"标签数据错误");
            }
        }
        infoModel = new ColumnInfoModel();
        infoModel.setName(name);
        infoModel.setAvatar(userModel.getAvatar());
        infoModel.setDescription(description);
        infoModel.setReviewed(true);
        infoModel.setCreatedUserId(userModel.getId());
        infoModel.setCreatedTime(DateUtils.getTimestamp());
        infoModel.setCreatedIp(IPUtils.toLong(IPUtils.getClientIp(request)));
        try {
            int result = infoService.insert(infoModel,tags);
            if(result == 1) return new ResultModel(infoModel);
            return new ResultModel(0,"创建失败");
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/setting")
    public ResultModel setting(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody Long id, @MultiRequestBody(required = false) String avatar,
                               @MultiRequestBody(required = false) String name, @MultiRequestBody(required = false) String description,
                               @MultiRequestBody(required = false) String path){
        if(userModel == null){
            return new ResultModel(401,"未登录");
        }
        ColumnInfoModel infoModel = searchService.findById(id);
        if(infoModel == null) return new ResultModel(401,"不存在的专栏编号");
        if(name != null){
            if(!StringUtils.isNotEmpty(name)) return new ResultModel(0,"名称格式不正确",name);
            ColumnInfoModel nameModel = searchService.findByName(name);
            if(nameModel != null && !nameModel.getId().equals(infoModel.getId())){
                return new ResultModel(1001,"名称不可用",name);
            }
            name = name.trim();
        }

        String ip = IPUtils.getClientIp(request);
        ColumnInfoModel updateModel = new ColumnInfoModel(
               id,name,avatar,description,userModel.getId(),
                DateUtils.getTimestamp(),IPUtils.toLong(ip)
        );

        try {
            if(StringUtils.isNotEmpty(path) && (path.length() < 3 || path.length() > 30)){
                return new ResultModel(0,"路径长度为3-30位");
            }
            int result = path != null ? infoService.update(updateModel,path.toLowerCase()) : infoService.update(updateModel);
            if(result == 1){
                return new ResultModel(searchService.getDetail(id,null,userModel.getId()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResultModel(0,"更新失败");
    }
}
