package com.student.archive.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生成绩表
 */
@Data
public class StudentGrade {

    private Long pkGrade;
    private Long studentId;
    private String semester;
    private String courseName;
    private String courseType;
    private BigDecimal credit;
    private BigDecimal score;
    private BigDecimal gpa;
    private Long importUserId;
    private LocalDateTime importTime;

    // 扩展字段（非数据库列）
    private String studentName;
    private String studentNo;
}
