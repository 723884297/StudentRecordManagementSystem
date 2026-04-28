package com.student.archive.entity;

import lombok.Data;

/**
 * 班级表
 */
@Data
public class ClassInfo {

    private Long pkClass;
    private Long majorId;
    private String className;
    private String grade;
}
