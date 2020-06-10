package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;

public interface QuestionAnswerDocService {

    Long create(Long questionId, Long userId, String content, String html,String description,String remark, String save_type, Long ip) throws Exception;

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
    QuestionAnswerVersionModel addVersion(Long id, Long questionId, Long userId, String content, String html, String description, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception;

    QuestionAnswerVersionModel addVersion(Long id,Long questionId, Long userId, String content, String html,String description,String remark, String save_type, Long ip,Boolean isPublish,Boolean force) throws Exception;
}
