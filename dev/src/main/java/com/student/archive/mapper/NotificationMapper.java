package com.student.archive.mapper;

import com.student.archive.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<Notification> selectByTargetUserId(@Param("targetUserId") Long targetUserId, @Param("isRead") Integer isRead);

    int insert(Notification notification);

    int markAsRead(@Param("pkNotification") Long pkNotification);

    int markAllAsRead(@Param("targetUserId") Long targetUserId);

    int countUnread(@Param("targetUserId") Long targetUserId);
}
