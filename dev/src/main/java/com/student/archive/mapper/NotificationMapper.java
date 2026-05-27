package com.student.archive.mapper;

import com.student.archive.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<Notification> selectByTargetUserId(@Param("targetUserId") Long targetUserId, @Param("isRead") Integer isRead);

    List<Notification> selectBySenderId(@Param("senderId") Long senderId);

    int insert(Notification notification);

    int batchInsert(@Param("list") List<Notification> list);

    int markAsRead(@Param("pkNotification") Long pkNotification);

    int markAllAsRead(@Param("targetUserId") Long targetUserId);

    int countUnread(@Param("targetUserId") Long targetUserId);
}
