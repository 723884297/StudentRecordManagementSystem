package com.student.archive.service;

import com.student.archive.entity.SysRole;

import java.util.List;

public interface SysRoleService {

    SysRole getById(Long pkRole);

    List<SysRole> getAll();

    List<SysRole> getRolesByUserId(Long userId);

    void add(SysRole role);

    void update(SysRole role);

    void delete(Long pkRole);

    void assignRoles(Long userId, List<Long> roleIds);
}
