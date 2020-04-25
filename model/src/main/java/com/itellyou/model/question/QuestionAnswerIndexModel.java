package com.itellyou.model.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerIndexModel {
    private Long id;
    private Long questionId;
    private String title;
    private String content;
    private Long createdUserId = 0l;
}
