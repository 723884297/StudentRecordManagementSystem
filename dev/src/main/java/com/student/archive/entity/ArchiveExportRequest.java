package com.student.archive.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 档案导出申请表
 */
@Data
public class ArchiveExportRequest {

    private Long pkRequest;
    private Long studentId;
    private String requestType;
    private String reason;
    private Integer status;        // 0-待审核 1-已通过 2-已驳回
    private Long auditUserId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;

    // 关联字段（JOIN查询填充）
    private String studentName;
    private String studentNo;
}
