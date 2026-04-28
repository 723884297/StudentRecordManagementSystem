package com.student.archive.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.archive.common.BusinessException;
import com.student.archive.common.PageResult;
import com.student.archive.entity.SysUser;
import com.student.archive.mapper.SysUserMapper;
import com.student.archive.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SysUser getById(Long pkUser) {
        return sysUserMapper.selectById(pkUser);
    }

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public PageResult<SysUser> getList(int pageNum, int pageSize, String username, String realName, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserMapper.selectList(username, realName, status);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    @Override
    @Transactional
    public void add(SysUser user) {
        SysUser existing = sysUserMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        sysUserMapper.insert(user);
    }

    @Override
    @Transactional
    public void update(SysUser user) {
        user.setPassword(null);
        sysUserMapper.update(user);
    }

    @Override
    @Transactional
    public void delete(Long pkUser) {
        sysUserMapper.deleteById(pkUser);
    }

    @Override
    @Transactional
    public void updateStatus(Long pkUser, Integer status) {
        sysUserMapper.updateStatus(pkUser, status);
    }

    @Override
    @Transactional
    public void resetPassword(Long pkUser, String newPassword) {
        SysUser user = new SysUser();
        user.setPkUser(pkUser);
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.update(user);
    }
}
