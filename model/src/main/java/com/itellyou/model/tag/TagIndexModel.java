package com.itellyou.model.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagIndexModel {
    private Long id;
    private Long groupId;
    private String name;
    private String content;
    private Long createdUserId;
}
