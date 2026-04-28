-- =============================================
-- 学生档案管理系统 - 数据库初始化脚本
-- 说明：基于原始设计优化，调整建表顺序，去除 sys_user.user_type 冗余字段，
--       为档案文件增加审核状态，补充索引及部分字段扩展。
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `StudentRecordManagementSystem` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `StudentRecordManagementSystem`;

-- 1. 用户表
CREATE TABLE `sys_user` (
  `pk_user` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(255) NOT NULL COMMENT '加密密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `status` TINYINT DEFAULT 1 COMMENT '1-启用,0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`pk_user`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 角色表
CREATE TABLE `sys_role` (
  `pk_role` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`pk_role`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 用户角色关联表
CREATE TABLE `sys_user_role` (
  `pk_user_role` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`pk_user_role`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`pk_role`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 4. 权限表（菜单/按钮）
CREATE TABLE `sys_permission` (
  `pk_permission` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `perm_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `perm_code` VARCHAR(100) NOT NULL COMMENT '权限标识符',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID，0为顶级',
  `url` VARCHAR(255) DEFAULT NULL COMMENT '路由地址',
  `type` VARCHAR(20) DEFAULT NULL COMMENT 'menu/button',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`pk_permission`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 5. 角色权限关联表
CREATE TABLE `sys_role_permission` (
  `pk_role_perm` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `perm_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`pk_role_perm`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`perm_id`),
  KEY `idx_perm_id` (`perm_id`),
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`pk_role`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`perm_id`) REFERENCES `sys_permission` (`pk_permission`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 6. 学院表
CREATE TABLE `college` (
  `pk_college` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学院ID',
  `college_name` VARCHAR(100) NOT NULL COMMENT '学院名称',
  `college_code` VARCHAR(20) DEFAULT NULL COMMENT '学院代码',
  PRIMARY KEY (`pk_college`),
  UNIQUE KEY `uk_college_name` (`college_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院表';

-- 7. 专业表
CREATE TABLE `major` (
  `pk_major` BIGINT NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  `college_id` BIGINT NOT NULL COMMENT '所属学院ID',
  `major_name` VARCHAR(100) NOT NULL COMMENT '专业名称',
  `major_code` VARCHAR(20) DEFAULT NULL COMMENT '专业代码',
  PRIMARY KEY (`pk_major`),
  KEY `idx_college_id` (`college_id`),
  CONSTRAINT `fk_major_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`pk_college`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

-- 8. 班级表
CREATE TABLE `class_info` (
  `pk_class` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `major_id` BIGINT NOT NULL COMMENT '所属专业ID',
  `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
  `grade` VARCHAR(10) DEFAULT NULL COMMENT '年级',
  PRIMARY KEY (`pk_class`),
  KEY `idx_major_id` (`major_id`),
  CONSTRAINT `fk_class_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`pk_major`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 9. 学生主表
CREATE TABLE `student` (
  `pk_student` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '关联登录用户ID',
  `student_no` VARCHAR(20) NOT NULL COMMENT '学号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` CHAR(1) DEFAULT NULL COMMENT '性别（M-男, F-女）',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `enrollment_year` YEAR DEFAULT NULL COMMENT '入学年份',
  `school_length` TINYINT DEFAULT NULL COMMENT '学制（年）',
  `college_id` BIGINT DEFAULT NULL COMMENT '学院ID',
  `major_id` BIGINT DEFAULT NULL COMMENT '专业ID',
  `class_id` BIGINT DEFAULT NULL COMMENT '班级ID',
  `status` VARCHAR(20) DEFAULT '在读' COMMENT '学籍状态：在读/休学/退学/毕业',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`pk_student`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_college_id` (`college_id`),
  KEY `idx_major_id` (`major_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_status` (`status`),
  KEY `idx_enrollment_year` (`enrollment_year`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE SET NULL,
  CONSTRAINT `fk_student_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`pk_college`) ON DELETE RESTRICT,
  CONSTRAINT `fk_student_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`pk_major`) ON DELETE RESTRICT,
  CONSTRAINT `fk_student_class` FOREIGN KEY (`class_id`) REFERENCES `class_info` (`pk_class`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生主表';

-- 10. 学生详情子表
CREATE TABLE `student_detail` (
  `pk_student_detail` BIGINT NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `nation` VARCHAR(50) DEFAULT NULL COMMENT '民族',
  `political_status` VARCHAR(50) DEFAULT NULL COMMENT '政治面貌',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
  `home_address` VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
  `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`pk_student_detail`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  CONSTRAINT `fk_detail_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`pk_student`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生详情子表';

-- 11. 档案材料分类表
CREATE TABLE `archive_category` (
  `pk_category` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '材料名称',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  `is_required` TINYINT DEFAULT 1 COMMENT '是否必需(1-是,0-否)',
  PRIMARY KEY (`pk_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案材料分类表';

-- 12. 档案文件表（增加审核状态字段）
CREATE TABLE `archive_file` (
  `pk_archive_file` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `category_id` BIGINT NOT NULL COMMENT '材料分类ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '材料描述',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核,1-已通过,2-已驳回',
  `upload_user_id` BIGINT DEFAULT NULL COMMENT '上传人用户ID',
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `audit_user_id` BIGINT DEFAULT NULL COMMENT '审核人用户ID',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核意见/驳回原因',
  PRIMARY KEY (`pk_archive_file`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_archive_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`pk_student`) ON DELETE CASCADE,
  CONSTRAINT `fk_archive_category` FOREIGN KEY (`category_id`) REFERENCES `archive_category` (`pk_category`) ON DELETE RESTRICT,
  CONSTRAINT `fk_archive_uploader` FOREIGN KEY (`upload_user_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE SET NULL,
  CONSTRAINT `fk_archive_auditor` FOREIGN KEY (`audit_user_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案文件表';

-- 13. 操作日志表
CREATE TABLE `sys_operation_log` (
  `pk_log` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
  `operation` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '详细描述',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数(JSON)',
  `ip` VARCHAR(45) DEFAULT NULL COMMENT '操作IP',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`pk_log`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 14. 通知提醒表
CREATE TABLE `notification` (
  `pk_notification` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `target_user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `sender_id` BIGINT DEFAULT NULL COMMENT '发送者ID，系统通知可为空',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `content` TEXT DEFAULT NULL COMMENT '内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '0-未读,1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
  PRIMARY KEY (`pk_notification`),
  KEY `idx_target_user` (`target_user_id`),
  KEY `idx_sender_id` (`sender_id`),
  CONSTRAINT `fk_notification_target` FOREIGN KEY (`target_user_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE CASCADE,
  CONSTRAINT `fk_notification_sender` FOREIGN KEY (`sender_id`) REFERENCES `sys_user` (`pk_user`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知提醒表';