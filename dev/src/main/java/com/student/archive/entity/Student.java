package com.student.archive.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生主表
 */
@Data
public class Student {

    private Long pkStudent;
    private Long userId;
    private String studentNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private Integer enrollmentYear;
    private Integer schoolLength;
    private Long collegeId;
    private Long majorId;
    private Long classId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
