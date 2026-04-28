package com.student.archive.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知提醒表
 */
@Data
public class Notification {

    private Long pkNotification;
    private Long targetUserId;
    private Long senderId;
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
