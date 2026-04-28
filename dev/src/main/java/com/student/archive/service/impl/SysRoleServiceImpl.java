package com.student.archive.service.impl;

import com.student.archive.entity.SysRole;
import com.student.archive.entity.SysUserRole;
import com.student.archive.mapper.SysRoleMapper;
import com.student.archive.mapper.SysUserRoleMapper;
import com.student.archive.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public SysRole getById(Long pkRole) {
        return sysRoleMapper.selectById(pkRole);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleMapper.selectAll();
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return sysRoleMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void add(SysRole role) {
        sysRoleMapper.insert(role);
    }

    @Override
    @Transactional
    public void update(SysRole role) {
        sysRoleMapper.update(role);
    }

    @Override
    @Transactional
    public void delete(Long pkRole) {
        sysRoleMapper.deleteById(pkRole);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> list = roleIds.stream().map(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).toList();
            sysUserRoleMapper.batchInsert(list);
        }
    }
}
