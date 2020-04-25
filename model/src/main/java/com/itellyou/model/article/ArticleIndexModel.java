package com.itellyou.model.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleIndexModel {
    private Long id;
    private Long columnId;
    private String title;
    private String content;
    private Long createdUserId;
}
