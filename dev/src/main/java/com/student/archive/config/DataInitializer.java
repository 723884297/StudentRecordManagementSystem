package com.student.archive.config;

import com.student.archive.entity.Student;
import com.student.archive.entity.SysRole;
import com.student.archive.entity.SysUser;
import com.student.archive.entity.SysUserRole;
import com.student.archive.mapper.StudentMapper;
import com.student.archive.mapper.SysRoleMapper;
import com.student.archive.mapper.SysUserMapper;
import com.student.archive.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据初始化器
 * 应用启动时检查是否存在管理员账号，没有则自动创建
 * 同时确保默认角色存在，并为学生用户自动分配 student 角色
 */
@Slf4j
// @Component  // 测试数据已通过 test_data.sql 导入，不再需要启动时自动初始化
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultPassword = "123456";

        // 1. 确保默认角色存在
        ensureDefaultRoles();

        // 2. 创建默认管理员（仅首次）
        SysUser admin = sysUserMapper.selectByUsername("admin");
        if (admin == null) {
            log.info("未检测到管理员账号，正在创建默认管理员...");
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setRealName("超级管理员");
            user.setStatus(1);
            sysUserMapper.insert(user);
            admin = user;
            log.info("默认管理员已创建: admin / {}", defaultPassword);
        }

        // 3. 确保管理员有 admin 角色
        ensureUserHasRole(admin.getPkUser(), "admin");

        // 4. 为关联了学生记录的用户自动分配 student 角色
        assignStudentRoles();

        log.info("数据初始化完成");
    }

    private void ensureDefaultRoles() {
        List<SysRole> existingRoles = sysRoleMapper.selectAll();
        Map<String, SysRole> codeMap = existingRoles.stream()
                .collect(Collectors.toMap(SysRole::getRoleCode, r -> r));

        String[][] defaultRoles = {
                {"admin", "系统管理员", "系统管理员，拥有所有权限"},
                {"student", "学生", "学生用户，可查看个人档案信息"},
                {"counselor", "辅导员", "辅导员，可管理所负责学生的档案"},
                {"archive_manager", "档案管理员", "档案管理员，负责档案审核"},
        };

        for (String[] rd : defaultRoles) {
            if (!codeMap.containsKey(rd[0])) {
                SysRole role = new SysRole();
                role.setRoleCode(rd[0]);
                role.setRoleName(rd[1]);
                role.setDescription(rd[2]);
                sysRoleMapper.insert(role);
                log.info("创建默认角色: {} ({})", rd[1], rd[0]);
            }
        }
    }

    private void ensureUserHasRole(Long userId, String roleCode) {
        List<SysRole> userRoles = sysRoleMapper.selectByUserId(userId);
        boolean hasRole = userRoles.stream().anyMatch(r -> roleCode.equals(r.getRoleCode()));
        if (!hasRole) {
            List<SysRole> allRoles = sysRoleMapper.selectAll();
            SysRole targetRole = allRoles.stream()
                    .filter(r -> roleCode.equals(r.getRoleCode()))
                    .findFirst().orElse(null);
            if (targetRole != null) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(targetRole.getPkRole());
                sysUserRoleMapper.insert(ur);
                log.info("为用户ID={} 分配角色: {}", userId, roleCode);
            }
        }
    }

    private void assignStudentRoles() {
        // 查找所有关联了 user_id 的学生记录
        List<Student> students = studentMapper.selectList(null, null, null, null, null, null);
        for (Student stu : students) {
            if (stu.getUserId() != null) {
                ensureUserHasRole(stu.getUserId(), "student");
            }
        }
    }
}
