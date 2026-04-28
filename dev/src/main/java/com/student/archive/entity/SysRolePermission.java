package com.student.archive.entity;

import lombok.Data;

/**
 * 角色权限关联表
 */
@Data
public class SysRolePermission {

    private Long pkRolePerm;
    private Long roleId;
    private Long permId;
}
