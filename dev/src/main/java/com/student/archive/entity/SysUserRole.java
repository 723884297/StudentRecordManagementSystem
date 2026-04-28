package com.student.archive.entity;

import lombok.Data;

/**
 * 用户角色关联表
 */
@Data
public class SysUserRole {

    private Long pkUserRole;
    private Long userId;
    private Long roleId;
}
