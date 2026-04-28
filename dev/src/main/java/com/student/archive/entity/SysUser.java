package com.student.archive.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
public class SysUser {

    private Long pkUser;
    private String username;
    private String password;
    private String realName;
    private Integer status;
    private LocalDateTime createTime;
}
