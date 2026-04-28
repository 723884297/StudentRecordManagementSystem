package com.student.archive.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.archive.common.PageResult;
import com.student.archive.entity.Notification;
import com.student.archive.mapper.NotificationMapper;
import com.student.archive.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<Notification> getByTargetUserId(int pageNum, int pageSize, Long targetUserId, Integer isRead) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notification> list = notificationMapper.selectByTargetUserId(targetUserId, isRead);
        PageInfo<Notification> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    @Override
    @Transactional
    public void send(Notification notification) {
        notificationMapper.insert(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long pkNotification) {
        notificationMapper.markAsRead(pkNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long targetUserId) {
        notificationMapper.markAllAsRead(targetUserId);
    }

    @Override
    public int countUnread(Long targetUserId) {
        return notificationMapper.countUnread(targetUserId);
    }
}
