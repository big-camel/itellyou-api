package com.itellyou.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationActorsModel {
    private Long notificationId;
    private Long userId;
    private Long targetId;
}
