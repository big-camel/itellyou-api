package com.itellyou.model.common;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationGroupCountModel {
    private EntityAction action;
    private EntityType type;
    private int count;
}
