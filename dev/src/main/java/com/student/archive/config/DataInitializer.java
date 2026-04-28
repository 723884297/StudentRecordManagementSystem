package com.student.archive.config;

import com.student.archive.entity.SysUser;
import com.student.archive.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 应用启动时检查是否存在管理员账号，没有则自动创建
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultPassword = "123456";
        String encoded = passwordEncoder.encode(defaultPassword);
        log.info("当前 BCrypt 哈希 (123456): {}", encoded);

        SysUser admin = sysUserMapper.selectByUsername("admin");
        if (admin == null) {
            log.info("未检测到管理员账号，正在创建默认管理员...");
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setPassword(encoded);
            user.setRealName("超级管理员");
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("默认管理员已创建: admin / {}", defaultPassword);
        } else if (!passwordEncoder.matches(defaultPassword, admin.getPassword())) {
            // 密码哈希不正确（如 init_data.sql 中的哈希无效），自动修正
            log.warn("管理员密码哈希无效，正在重置为默认密码...");
            admin.setPassword(encoded);
            sysUserMapper.update(admin);
            log.info("管理员密码已重置为: {}", defaultPassword);
        } else {
            log.info("管理员账号已存在且密码正确，跳过初始化");
        }
    }
}
