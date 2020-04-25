package com.itellyou.model.user;

import com.itellyou.model.sys.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationDisplayModel {
    private Long userId;
    private UserOperationalAction action;
    private EntityType type;
    private UserNotificationDisplay value;
}
