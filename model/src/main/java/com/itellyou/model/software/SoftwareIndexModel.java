package com.itellyou.model.software;

import com.itellyou.model.common.IndexModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class SoftwareIndexModel extends IndexModel {
    private Long groupId;
    private String name;
    private String content;
    private Long createdUserId;
}
