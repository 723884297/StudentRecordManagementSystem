package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.Notification;
import com.student.archive.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/sent")
    public Result<PageResult<Notification>> sentList(@RequestParam(defaultValue = "1") int pageNum,
                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                     @RequestParam Long senderId) {
        return Result.success(notificationService.getBySenderId(pageNum, pageSize, senderId));
    }

    @PostMapping("/send")
    public Result<Void> send(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Long senderId = Long.valueOf(body.get("senderId").toString());
        @SuppressWarnings("unchecked")
        List<Number> targetUserIds = (List<Number>) body.get("targetUserIds");

        List<Notification> notifications = targetUserIds.stream().map(uid -> {
            Notification n = new Notification();
            n.setTargetUserId(uid.longValue());
            n.setSenderId(senderId);
            n.setTitle(title);
            n.setContent(content);
            return n;
        }).collect(Collectors.toList());

        notificationService.batchSend(notifications);
        return Result.success();
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
