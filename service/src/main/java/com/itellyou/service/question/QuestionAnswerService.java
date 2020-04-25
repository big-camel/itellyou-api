package com.itellyou.service.question;

import com.itellyou.model.sys.VoteType;
import com.itellyou.model.question.*;
import com.itellyou.model.view.ViewInfoModel;

import java.util.Map;

public interface QuestionAnswerService {
    int insert(QuestionAnswerModel answerModel);

    int updateView(Long userId, Long id, Long ip, String os, String browser) throws Exception;

    int updateComments(Long id, Integer value);

    int updateDisabled(Boolean isDisabled,Long id);

    int updateDeleted(Boolean isDeleted,Long id);

    int updateAdopted(Boolean isAdopted,Long id);

    int updateStarCountById(Long id,Integer step);

    QuestionDetailModel adopt(Long id,Long userId, String ip) throws Exception;

    QuestionAnswerDetailModel delete(Long id,Long questionId,Long userId) throws Exception;
    QuestionAnswerDetailModel revokeDelete(Long id,Long questionId,Long userId) throws Exception;

    Long create(Long questionId, Long userId, String content, String html,String description,String remark, String save_type, Long ip) throws Exception;

    Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip);

    int updateMetas(Long id, String cover);
}
