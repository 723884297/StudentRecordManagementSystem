package com.student.archive.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生奖惩表
 */
@Data
public class StudentAward {

    private Long pkAward;
    private Long studentId;
    private String type;  // award-奖励, punishment-处分
    private String awardName;
    private String awardLevel;
    private LocalDate awardDate;
    private String issuingAuthority;
    private String description;
    private Long importUserId;
    private LocalDateTime importTime;

    // 扩展字段（非数据库列）
    private String studentName;
    private String studentNo;
}
