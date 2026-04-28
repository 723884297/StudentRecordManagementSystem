package com.student.archive.entity;

import lombok.Data;

/**
 * 角色表
 */
@Data
public class SysRole {

    private Long pkRole;
    private String roleName;
    private String roleCode;
    private String description;
}
