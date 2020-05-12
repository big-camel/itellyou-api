package com.itellyou.model.column;

import com.itellyou.model.common.IndexModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ColumnIndexModel extends IndexModel {
    private Long id;
    private String name;
    private String description;
    private Long createdUserId;
}
