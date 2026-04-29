package com.student.archive.controller;

import com.student.archive.common.BusinessException;
import com.student.archive.common.Result;
import com.student.archive.entity.SysRole;
import com.student.archive.entity.SysUser;
import com.student.archive.service.SysRoleService;
import com.student.archive.service.SysUserService;
import com.student.archive.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || username.isBlank()) {
            throw new BusinessException(1001, "请输入用户名");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException(1002, "请输入密码");
        }

        // 查询用户
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new BusinessException(1003, "账号不存在，请检查用户名");
        }

        // 检查状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(1004, "该账号已被禁用，请联系管理员");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(1005, "密码错误，请重新输入");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getPkUser(), user.getUsername());

        // 查询用户角色
        List<SysRole> roles = sysRoleService.getRolesByUserId(user.getPkUser());
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();

        // 返回用户信息和 Token
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getPkUser());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("roles", roleCodes);
        return Result.success("登录成功", data);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 查询用户角色
        List<SysRole> roles = sysRoleService.getRolesByUserId(userId);
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getPkUser());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("status", user.getStatus());
        data.put("roles", roleCodes);
        return Result.success(data);
    }
}
