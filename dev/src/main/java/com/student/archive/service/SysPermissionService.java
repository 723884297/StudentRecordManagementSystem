package com.student.archive.service;

import com.student.archive.entity.SysPermission;

import java.util.List;

public interface SysPermissionService {

    List<SysPermission> getAll();

    List<SysPermission> getByUserId(Long userId);

    void add(SysPermission permission);

    void update(SysPermission permission);

    void delete(Long pkPermission);

    void assignPermissions(Long roleId, List<Long> permissionIds);
}
