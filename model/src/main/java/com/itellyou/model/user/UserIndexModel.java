package com.itellyou.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIndexModel {
    private Long id;
    private String name;
    private String description;
}
