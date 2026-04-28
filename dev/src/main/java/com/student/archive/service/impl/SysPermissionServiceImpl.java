package com.student.archive.service.impl;

import com.student.archive.entity.SysPermission;
import com.student.archive.entity.SysRolePermission;
import com.student.archive.mapper.SysPermissionMapper;
import com.student.archive.mapper.SysRolePermissionMapper;
import com.student.archive.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysPermission> getAll() {
        return sysPermissionMapper.selectAll();
    }

    @Override
    public List<SysPermission> getByUserId(Long userId) {
        return sysPermissionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void add(SysPermission permission) {
        sysPermissionMapper.insert(permission);
    }

    @Override
    @Transactional
    public void update(SysPermission permission) {
        sysPermissionMapper.update(permission);
    }

    @Override
    @Transactional
    public void delete(Long pkPermission) {
        sysPermissionMapper.deleteById(pkPermission);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> list = permissionIds.stream().map(permId -> {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermId(permId);
                return rp;
            }).toList();
            sysRolePermissionMapper.batchInsert(list);
        }
    }
}
