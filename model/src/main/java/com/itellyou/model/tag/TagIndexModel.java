package com.itellyou.model.tag;

import com.itellyou.model.common.IndexModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class TagIndexModel extends IndexModel {
    private Long id;
    private Long groupId;
    private String name;
    private String content;
    private Long createdUserId;
}
