package com.student.archive.service;

import com.student.archive.common.PageResult;
import com.student.archive.entity.Notification;

public interface NotificationService {

    PageResult<Notification> getByTargetUserId(int pageNum, int pageSize, Long targetUserId, Integer isRead);

    void send(Notification notification);

    void markAsRead(Long pkNotification);

    void markAllAsRead(Long targetUserId);

    int countUnread(Long targetUserId);
}
