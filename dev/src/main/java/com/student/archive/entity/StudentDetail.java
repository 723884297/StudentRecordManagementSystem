package com.student.archive.entity;

import lombok.Data;

/**
 * 学生详情子表
 */
@Data
public class StudentDetail {

    private Long pkStudentDetail;
    private Long studentId;
    private String nation;
    private String politicalStatus;
    private String phone;
    private String email;
    private String homeAddress;
    private String emergencyContact;
    private String emergencyPhone;
    private String remark;
}
