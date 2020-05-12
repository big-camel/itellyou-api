package com.itellyou.model.question;

import com.itellyou.model.common.IndexModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionAnswerIndexModel extends IndexModel {
    private Long id;
    private Long questionId;
    private String title;
    private String content;
    private Long createdUserId = 0l;
}
