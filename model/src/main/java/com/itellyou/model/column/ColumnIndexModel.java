package com.itellyou.model.column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnIndexModel {
    private Long id;
    private String name;
    private String description;
    private Long createdUserId;
}
