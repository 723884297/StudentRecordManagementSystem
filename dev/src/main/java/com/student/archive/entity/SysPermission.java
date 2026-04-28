package com.student.archive.entity;

import lombok.Data;

/**
 * 权限表
 */
@Data
public class SysPermission {

    private Long pkPermission;
    private String permName;
    private String permCode;
    private Long parentId;
    private String url;
    private String type;
    private String icon;
    private Integer sortOrder;
}
