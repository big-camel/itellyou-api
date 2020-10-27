package com.itellyou.model.question;

import com.itellyou.model.common.IndexModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;
import org.apache.lucene.document.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JSONDefault(includes = "base")
public class QuestionAnswerIndexModel extends IndexModel {
    private Long questionId;
    private Long createdUserId = 0l;

    public QuestionAnswerIndexModel(Document document){
        super(document);
        this.setType(EntityType.ANSWER);
        String userId = document.get("created_user_id");
        this.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        String questionId = document.get("question_id");
        this.setQuestionId(StringUtils.isNotEmpty(questionId) ? Long.parseLong(questionId) : 0);
    }
}
