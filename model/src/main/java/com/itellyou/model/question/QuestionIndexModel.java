package com.itellyou.model.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionIndexModel {
    private Long id;
    private String title;
    private String content;
    private Long createdUserId;
}
