package com.student.archive.service;

import com.student.archive.common.PageResult;
import com.student.archive.entity.Notification;

import java.util.List;

public interface NotificationService {

    PageResult<Notification> getByTargetUserId(int pageNum, int pageSize, Long targetUserId, Integer isRead);

    PageResult<Notification> getBySenderId(int pageNum, int pageSize, Long senderId);

    void send(Notification notification);

    void batchSend(List<Notification> notifications);

    void markAsRead(Long pkNotification);

    void markAllAsRead(Long targetUserId);

    int countUnread(Long targetUserId);
}
