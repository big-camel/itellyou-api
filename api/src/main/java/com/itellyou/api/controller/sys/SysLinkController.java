package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysLinkModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysLinkService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/system/link")
public class SysLinkController {

    private final SysLinkService linkService;

    public SysLinkController(SysLinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("")
    public ResultModel get(@RequestParam Map params) {
        Integer offset = Params.getOrDefault(params, "offset", Integer.class, 0);
        Integer limit = Params.getOrDefault(params, "limit", Integer.class, 20);
        String text = Params.getOrDefault(params, "text", String.class, null);
        String link = Params.getOrDefault(params, "link", String.class, null);
        String target = Params.getOrDefault(params, "target", String.class, null);
        String beginTime = Params.getOrDefault(params,"begin",String.class,null);
        Long begin = DateUtils.getTimestamp(beginTime);
        String endTime = Params.getOrDefault(params,"end",String.class,null);
        Long end = DateUtils.getTimestamp(endTime);
        String ip = Params.getOrDefault(params,"ip",String.class,null);
        Long ipLong = IPUtils.toLong(ip,null);
        return new ResultModel(linkService.page(null,text,link,target,null,begin,end,ipLong,null,offset,limit));
    }

    @PutMapping("")
    public ResultModel add(UserInfoModel userMode, HttpServletRequest request, @RequestBody Map<String,Object> params){
        String text = Params.getOrDefault(params,"text","");
        String link = Params.getOrDefault(params,"link","");
        String target = Params.getOrDefault(params,"target","_blank");

        SysLinkModel linkModel = new SysLinkModel();
        linkModel.setText(text);
        linkModel.setLink(link);
        linkModel.setTarget(target);
        linkModel.setCreatedUserId(userMode.getId());
        linkModel.setCreatedTime(DateUtils.getTimestamp());
        linkModel.setCreatedIp(IPUtils.toLong(request));

        int result = linkService.insert(linkModel);
        if(result == 1) return new ResultModel();
        return new ResultModel(500,"新增失败");
    }

    @DeleteMapping("")
    public ResultModel delete(@RequestParam Long id){
        int result = linkService.delete(id);
        if(result == 1) return new ResultModel();
        return new ResultModel(500,"删除失败");
    }
}
