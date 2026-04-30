package com.student.archive.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 档案文件表
 */
@Data
public class ArchiveFile {

    private Long pkArchiveFile;
    private Long studentId;
    private Long categoryId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String description;
    private Integer status;
    private Long uploadUserId;
    private LocalDateTime uploadTime;
    private Long auditUserId;
    private LocalDateTime auditTime;
    private String auditRemark;

    // 关联字段（非DB字段，JOIN查询填充）
    private String studentName;
    private String studentNo;
    private String categoryName;
}
