package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface QuestionAnswerVersionService {
    int insert(QuestionAnswerVersionModel versionModel);

    Integer findVersionById(Long id);

    List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId,Long questionId, Boolean hasContent);

    List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId, Boolean hasContent);

    List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId,Long questionId);

    List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId);

    QuestionAnswerVersionModel findById(Long id);

    QuestionAnswerVersionModel findByAnswerIdAndId(Long id, Long answerId);

    QuestionAnswerVersionModel findByAnswerIdAndId(Long id, Long answerId,Long questionId);

    int updateVersion(Long id, Integer version, Long ip, Long user);

    int updateVersion(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long ip, Long user);

    int updateVersion(Long id, Integer version, Integer draft, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user);

    int updateVersion(QuestionAnswerVersionModel versionModel);

    int updateDraft(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user);

    int updateDraft(Long id, Integer version, Long time, Long ip, Long user);

    int updateDraft(QuestionAnswerVersionModel versionModel);

    /**
     * 新增版本，无内容更新将不新增版本，在用户删除草稿此回答情况下，会将其恢复正常状态
     * @param id
     * @param questionId
     * @param userId
     * @param content
     * @param html
     * @param remark
     * @param save_type
     * @param ip
     * @return
     * @throws Exception
     */
    QuestionAnswerVersionModel addVersion(Long id,Long questionId, Long userId, String content, String html,String description,String remark,Integer version, String save_type, Long ip,Boolean isPublish,Boolean force) throws Exception;

    QuestionAnswerVersionModel addVersion(Long id,Long questionId, Long userId, String content, String html,String description,String remark, String save_type, Long ip,Boolean isPublish,Boolean force) throws Exception;

}
