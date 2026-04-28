package com.student.archive.service;

import com.student.archive.common.PageResult;
import com.student.archive.entity.SysUser;

public interface SysUserService {

    SysUser getById(Long pkUser);

    SysUser getByUsername(String username);

    PageResult<SysUser> getList(int pageNum, int pageSize, String username, String realName, Integer status);

    void add(SysUser user);

    void update(SysUser user);

    void delete(Long pkUser);

    void updateStatus(Long pkUser, Integer status);

    void resetPassword(Long pkUser, String newPassword);
}
