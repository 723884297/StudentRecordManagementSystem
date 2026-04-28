package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.Notification;
import com.student.archive.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<PageResult<Notification>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                 @RequestParam Long targetUserId,
                                                 @RequestParam(required = false) Integer isRead) {
        return Result.success(notificationService.getByTargetUserId(pageNum, pageSize, targetUserId, isRead));
    }

    @GetMapping("/unread/count")
    public Result<Integer> countUnread(@RequestParam Long targetUserId) {
        return Result.success(notificationService.countUnread(targetUserId));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    @PutMapping("/readAll")
    public Result<Void> markAllAsRead(@RequestParam Long targetUserId) {
        notificationService.markAllAsRead(targetUserId);
        return Result.success();
    }
}
