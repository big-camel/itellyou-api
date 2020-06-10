package com.itellyou.api.controller.question;

import com.itellyou.api.handler.TokenAccessDeniedException;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.sys.RewardConfigModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.collab.CollabInfoService;
import com.itellyou.service.question.*;
import com.itellyou.service.sys.RewardConfigService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.service.user.UserSingleService;
import com.itellyou.service.user.access.UserPermissionService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/question")
public class QuestionDocController {
    private final QuestionSearchService questionSearchService;
    private final CollabInfoService collabInfoService;
    private final RewardConfigService rewardService;
    private final UserSingleService userSearchService;
    private final UserBankService bankService;
    private final UserPermissionService userPermissionService;
    private final QuestionDocService docService;
    private final TagSingleService tagSingleService;
    private final QuestionSingleService questionSingleService;
    private final QuestionVersionSearchService versionSearchService;

    @Autowired
    public QuestionDocController(QuestionSearchService questionSearchService, CollabInfoService collabInfoService, RewardConfigService rewardService, UserSingleService userSearchService, UserBankService bankService, UserPermissionService userPermissionService, QuestionDocService docService, TagSingleService tagSingleService, QuestionSingleService questionSingleService, QuestionVersionSearchService versionSearchService){
        this.questionSearchService = questionSearchService;
        this.collabInfoService = collabInfoService;
        this.rewardService = rewardService;
        this.userSearchService = userSearchService;
        this.bankService = bankService;
        this.userPermissionService = userPermissionService;
        this.docService = docService;
        this.tagSingleService = tagSingleService;
        this.questionSingleService = questionSingleService;
        this.versionSearchService = versionSearchService;
    }

    private boolean checkAuthority(Long authorId,Long userId){
        if(!authorId.equals(userId) && !userPermissionService.check(userId,"web_question_public_edit")){
            throw new TokenAccessDeniedException(403,"无权限编辑");
        }
        return true;
    }

    @PostMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userInfoModel, @MultiRequestBody(required = false) String title, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);

        try{
            Long id = docService.create(userInfoModel.getId(),title,content,html,"",RewardType.DEFAULT,0.0,0.0,null
                    ,"创建提问",save_type,ipLong);
            if(id == null) return new ResultModel(0,"创建失败");
            return new ResultModel(id);
        }catch (Exception e){
            return new ResultModel(0,"创建失败，" + e.getMessage());
        }
    }

    @GetMapping("/{id:\\d+}/edit")
    public ResultModel draft(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @RequestParam(required = false)  boolean ot){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        QuestionDetailModel questionDetailModel = questionSearchService.getDetail(id,"draft");
        if(questionDetailModel == null || questionDetailModel.isDeleted() || questionDetailModel.isDisabled()){
            return  new ResultModel(404,"无记录，错误的ID");
        }

        checkAuthority(questionDetailModel.getCreatedUserId(),userInfoModel.getId());

        if(ot){
            String clientIp = IPUtils.getClientIp(request);
            String key = "question/" + questionDetailModel.getId();
            CollabInfoModel collabInfoModel = collabInfoService.createDefault(key,userInfoModel.getId(),clientIp);
            if(collabInfoModel == null){
                return new ResultModel(0,"创建协作失败");
            }
            questionDetailModel.setCollab(collabInfoModel);
        }
        return new ResultModel(questionDetailModel,new Labels.LabelModel(QuestionDetailModel.class,"draft","collab"));
    }

    @PutMapping("/{id:\\d+}/content")
    public ResultModel content(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody(required = false) String title, @MultiRequestBody String content, @MultiRequestBody String html, @MultiRequestBody String save_type){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        String clientIp = IPUtils.getClientIp(request);
        Long ipLong = IPUtils.toLong(clientIp);
        QuestionInfoModel infoModel = questionSingleService.findById(id);
        try{
            if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用提问");

            checkAuthority(infoModel.getCreatedUserId(),userInfoModel.getId());

            QuestionVersionModel versionModel = docService.addVersion(id,userInfoModel.getId(),title,content,html,null,null,null,null,null,
                    "一般编辑更新",null,save_type,ipLong,false,false);
            if(versionModel == null) return new ResultModel(0,"更新内容失败");
            QuestionDetailModel detailModel = questionSearchService.getDetail(id,"draft");
            return new ResultModel(detailModel,new Labels.LabelModel(QuestionDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/rollback")
    public ResultModel rollback(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody Long version_id){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        QuestionInfoModel infoModel = questionSingleService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()) return new ResultModel(404,"无可用提问");

        checkAuthority(infoModel.getCreatedUserId(),userInfoModel.getId());

        QuestionVersionModel questionVersion = versionSearchService.findByQuestionIdAndId(version_id,id);
        if(questionVersion == null || questionVersion.isDisabled()){
            return  new ResultModel(0,"无记录，错误的ID");
        }
        String clientIp = IPUtils.getClientIp(request);
        try {
            HashSet<Long> tagIds = new LinkedHashSet<>();
            for(TagDetailModel tagDetailModel : questionVersion.getTags()){
                tagIds.add(tagDetailModel.getId());
            }
            QuestionVersionModel versionModel = docService.addVersion(id,userInfoModel.getId(),
                    questionVersion.getTitle(),questionVersion.getContent(),questionVersion.getHtml(),questionVersion.getDescription(),
                    questionVersion.getRewardType(),questionVersion.getRewardValue(),0.0,tagIds,
                    "回滚到版本[" + questionVersion.getVersion() + "]",null,"rollback",
                    IPUtils.toLong(clientIp),false,true);

            if(versionModel == null) return new ResultModel(0,"回滚失败");
            QuestionDetailModel detailModel = questionSearchService.getDetail(id,"draft");
            return new ResultModel(detailModel,new Labels.LabelModel(QuestionDetailModel.class,"draft"));
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}/publish")
    public ResultModel publish(HttpServletRequest request, UserInfoModel userInfoModel, @PathVariable Long id, @MultiRequestBody @NotNull Long[] tags, @MultiRequestBody @NotNull Map<String,Object> reward, @MultiRequestBody(required = false) String remark){
        if(userInfoModel == null){
            return new ResultModel(401,"未登录");
        }
        QuestionDetailModel questionVersion = questionSearchService.getDetail(id,"draft");
        if(questionVersion == null || questionVersion.isDisabled() || questionVersion.isDeleted()){
            return new ResultModel(401,"无权限");
        }

        checkAuthority(questionVersion.getCreatedUserId(),userInfoModel.getId());

        if(tags.length > 0){
            int rows = tagSingleService.exists(tags);
            if(rows != tags.length){
                return new ResultModel(0,"标签数据错误");
            }
        }

        HashSet<Long> tagIds = new LinkedHashSet<>();
        for(Object tagId : tags){
            tagIds.add((Long)tagId);
        }

        Object objType = reward.get("type");
        RewardType rewardType = objType == null ? RewardType.DEFAULT : RewardType.valueOf(objType.toString().toUpperCase());
        if(rewardType == null){
            return new ResultModel(0,"错误的悬赏类别");
        }
        Map<RewardType, RewardConfigModel> rewardConfig = rewardService.findByDefault();
        if(rewardConfig == null){
            return new ResultModel(0,"获取悬赏配置错误");
        }
        UserBankModel bankModel = bankService.findByUserId(userInfoModel.getId());
        Object objValue = reward.get("value");
        Double rewardValue =  objValue == null ? 0 : Math.abs(Double.parseDouble(objValue.toString()));
        if(rewardType == RewardType.DEFAULT){
            rewardValue = 0.0;
        }else if(rewardType == RewardType.CREDIT){
            RewardConfigModel config = rewardConfig.get(RewardType.CREDIT);
            if(rewardValue < config.getMin()){
                return new ResultModel(0,"积分悬赏最低" + rewardValue + config.getUnit());
            }
            if(rewardValue > config.getMax()){
                return new ResultModel(0,"积分悬赏最高不超过" + rewardValue + config.getUnit());
            }
            if(bankModel != null && bankModel.getCredit() < rewardValue){
                return new ResultModel(1001,"积分余额不足",bankModel);
            }
        }else if(rewardType == RewardType.CASH){
            RewardConfigModel config = rewardConfig.get(RewardType.CASH);
            if(rewardValue < config.getMin()){
                return new ResultModel(0,"现金悬赏最低" + rewardValue + config.getUnit());
            }
            if(rewardValue > config.getMax()){
                return new ResultModel(0,"现金悬赏最高不超过" + rewardValue + config.getUnit());
            }
            if(bankModel != null && bankModel.getCash() < rewardValue){
                return new ResultModel(1002,"用户余额不足",bankModel);
            }
        }

        if(questionVersion.getRewardType() != RewardType.DEFAULT){
            rewardType = questionVersion.getRewardType();
        }
        if(StringUtils.isEmpty(remark)) remark = "发布";
        String clientIp = IPUtils.getClientIp(request);
        try {
            QuestionVersionModel versionModel = docService.addVersion(id,userInfoModel.getId(),
                    questionVersion.getTitle(),questionVersion.getContent(),questionVersion.getHtml(),questionVersion.getDescription(),
                    rewardType,questionVersion.getRewardValue() + rewardValue,rewardValue,tagIds,
                    remark,null,"publish",
                    IPUtils.toLong(clientIp),true,true);

            if(versionModel == null) return new ResultModel(0,"发布提问失败");
            QuestionDetailModel detailModel = questionSearchService.getDetail(id,"version");
            return new ResultModel(detailModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PostMapping("/{id:\\d+}/collab")
    public ResultModel collab(@MultiRequestBody @NotBlank String token, @PathVariable Long id){
        CollabInfoModel collabInfoModel = collabInfoService.findByToken(token);
        if(collabInfoModel == null || collabInfoModel.isDisabled() == true){
            return new ResultModel(0,"错误的Token");
        }
        QuestionInfoModel infoModel = questionSingleService.findById(id);
        if(infoModel == null || infoModel.isDisabled() || infoModel.isDeleted()){
            return new ResultModel(0,"错误的文档");
        }
        UserInfoModel userInfo = userSearchService.findById(collabInfoModel.getCreatedUserId());
        if(userInfo == null || userInfo.isDisabled()){
            return new ResultModel(0,"用户状态不正确");
        }

        checkAuthority(infoModel.getCreatedUserId(),userInfo.getId());

        Map<String,Object> mapData = new HashMap<>();
        mapData.put("doc",infoModel);
        mapData.put("user",userInfo);
        return new ResultModel(mapData);
    }
}
