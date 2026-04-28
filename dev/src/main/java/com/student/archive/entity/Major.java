package com.student.archive.entity;

import lombok.Data;

/**
 * 专业表
 */
@Data
public class Major {

    private Long pkMajor;
    private Long collegeId;
    private String majorName;
    private String majorCode;
}
