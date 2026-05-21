-- =============================================
-- 学生档案管理系统 - 数据库脚本（重构版）
-- 设计原则：不使用外键，通过应用层保证数据一致性
--           主子表分开，索引保留用于查询性能
-- =============================================

CREATE DATABASE IF NOT EXISTS `StudentRecordManagementSystem`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `StudentRecordManagementSystem`;

-- 禁用外键检查（兼容旧表有外键的情况）
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 一、系统管理模块
-- =============================================

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `pk_user`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`    VARCHAR(255) NOT NULL COMMENT '加密密码（BCrypt）',
  `real_name`   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`pk_user`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `pk_role`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
  `role_code`   VARCHAR(50)  NOT NULL COMMENT '角色编码（admin/student/counselor/archive_manager）',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`pk_role`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 用户-角色 关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `pk_user_role` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id`      BIGINT NOT NULL COMMENT '用户ID → sys_user.pk_user',
  `role_id`      BIGINT NOT NULL COMMENT '角色ID → sys_role.pk_role',
  PRIMARY KEY (`pk_user_role`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 4. 权限表（菜单/按钮）
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `pk_permission` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `perm_name`     VARCHAR(50)  NOT NULL COMMENT '权限名称',
  `perm_code`     VARCHAR(100) NOT NULL COMMENT '权限标识符',
  `parent_id`     BIGINT       DEFAULT 0 COMMENT '父权限ID，0为顶级',
  `url`           VARCHAR(255) DEFAULT NULL COMMENT '路由地址',
  `type`          VARCHAR(20)  DEFAULT NULL COMMENT '类型：menu/button',
  `icon`          VARCHAR(50)  DEFAULT NULL COMMENT '图标',
  `sort_order`    INT          DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`pk_permission`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 5. 角色-权限 关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `pk_role_perm` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id`      BIGINT NOT NULL COMMENT '角色ID → sys_role.pk_role',
  `perm_id`      BIGINT NOT NULL COMMENT '权限ID → sys_permission.pk_permission',
  PRIMARY KEY (`pk_role_perm`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`),
  KEY `idx_role_perm_perm` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =============================================
-- 二、组织架构模块
-- =============================================

-- 6. 学院表
DROP TABLE IF EXISTS `college`;
CREATE TABLE `college` (
  `pk_college`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '学院ID',
  `college_name` VARCHAR(100) NOT NULL COMMENT '学院名称',
  `college_code` VARCHAR(20)  DEFAULT NULL COMMENT '学院代码',
  PRIMARY KEY (`pk_college`),
  UNIQUE KEY `uk_college_name` (`college_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院表';

-- 7. 专业表
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major` (
  `pk_major`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  `college_id`  BIGINT       NOT NULL COMMENT '所属学院ID → college.pk_college',
  `major_name`  VARCHAR(100) NOT NULL COMMENT '专业名称',
  `major_code`  VARCHAR(20)  DEFAULT NULL COMMENT '专业代码',
  PRIMARY KEY (`pk_major`),
  KEY `idx_major_college` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

-- 8. 班级表
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info` (
  `pk_class`   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `major_id`   BIGINT      NOT NULL COMMENT '所属专业ID → major.pk_major',
  `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
  `grade`      VARCHAR(10) DEFAULT NULL COMMENT '年级',
  PRIMARY KEY (`pk_class`),
  KEY `idx_class_major` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- =============================================
-- 三、学生管理模块（主子表分离）
-- =============================================

-- 9. 学生主表（学籍核心信息）
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `pk_student`      BIGINT      NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id`         BIGINT      DEFAULT NULL COMMENT '关联用户ID → sys_user.pk_user',
  `student_no`      VARCHAR(20) NOT NULL COMMENT '学号',
  `name`            VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender`          CHAR(1)     DEFAULT NULL COMMENT '性别：M-男 F-女',
  `birth_date`      DATE        DEFAULT NULL COMMENT '出生日期',
  `id_card`         VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `enrollment_year` YEAR        DEFAULT NULL COMMENT '入学年份',
  `school_length`   TINYINT     DEFAULT NULL COMMENT '学制（年）',
  `college_id`      BIGINT      DEFAULT NULL COMMENT '学院ID → college.pk_college',
  `major_id`        BIGINT      DEFAULT NULL COMMENT '专业ID → major.pk_major',
  `class_id`        BIGINT      DEFAULT NULL COMMENT '班级ID → class_info.pk_class',
  `status`          VARCHAR(20) DEFAULT '在读' COMMENT '学籍状态：在读/休学/退学/毕业',
  `create_time`     DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time`     DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`pk_student`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_student_college` (`college_id`),
  KEY `idx_student_major` (`major_id`),
  KEY `idx_student_class` (`class_id`),
  KEY `idx_student_status` (`status`),
  KEY `idx_student_enrollment` (`enrollment_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生主表';

-- 10. 学生详情子表（联系方式、家庭信息）
DROP TABLE IF EXISTS `student_detail`;
CREATE TABLE `student_detail` (
  `pk_student_detail` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `student_id`        BIGINT       NOT NULL COMMENT '学生ID → student.pk_student',
  `nation`            VARCHAR(50)  DEFAULT NULL COMMENT '民族',
  `political_status`  VARCHAR(50)  DEFAULT NULL COMMENT '政治面貌',
  `phone`             VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `email`             VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
  `home_address`      VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
  `emergency_contact` VARCHAR(50)  DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '紧急联系电话',
  `remark`            TEXT         DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`pk_student_detail`),
  UNIQUE KEY `uk_detail_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生详情子表';

-- =============================================
-- 四、档案管理模块
-- =============================================

-- 11. 档案材料分类表（字典表）
DROP TABLE IF EXISTS `archive_category`;
CREATE TABLE `archive_category` (
  `pk_category`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '材料名称',
  `sort_order`    INT          DEFAULT 0 COMMENT '排序号',
  `is_required`   TINYINT      DEFAULT 1 COMMENT '是否必需：1-是 0-否',
  PRIMARY KEY (`pk_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案材料分类表';

-- 12. 档案文件表
DROP TABLE IF EXISTS `archive_file`;
CREATE TABLE `archive_file` (
  `pk_archive_file` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `student_id`      BIGINT       NOT NULL COMMENT '学生ID → student.pk_student',
  `category_id`     BIGINT       NOT NULL COMMENT '材料分类ID → archive_category.pk_category',
  `file_name`       VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_path`       VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_size`       BIGINT       DEFAULT NULL COMMENT '文件大小（字节）',
  `description`     VARCHAR(255) DEFAULT NULL COMMENT '材料描述',
  `status`          TINYINT      DEFAULT 0 COMMENT '审核状态：0-待审核 1-已通过 2-已驳回',
  `upload_user_id`  BIGINT       DEFAULT NULL COMMENT '上传人ID → sys_user.pk_user',
  `upload_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `audit_user_id`   BIGINT       DEFAULT NULL COMMENT '审核人ID → sys_user.pk_user',
  `audit_time`      DATETIME     DEFAULT NULL COMMENT '审核时间',
  `audit_remark`    VARCHAR(500) DEFAULT NULL COMMENT '审核意见/驳回原因',
  PRIMARY KEY (`pk_archive_file`),
  KEY `idx_archive_student` (`student_id`),
  KEY `idx_archive_category` (`category_id`),
  KEY `idx_archive_status` (`status`),
  KEY `idx_archive_upload_time` (`upload_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案文件表';

-- =============================================
-- 4.5 成绩与奖项模块
-- =============================================

-- 15. 学生成绩表
DROP TABLE IF EXISTS `student_grade`;
CREATE TABLE `student_grade` (
  `pk_grade`       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id`     BIGINT       NOT NULL COMMENT '学生ID → student.pk_student',
  `semester`       VARCHAR(20)  NOT NULL COMMENT '学期（如 2024-2025-1）',
  `course_name`    VARCHAR(100) NOT NULL COMMENT '课程名称',
  `course_type`    VARCHAR(20)  DEFAULT NULL COMMENT '课程类型：必修/选修/公共',
  `credit`         DECIMAL(3,1) DEFAULT NULL COMMENT '学分',
  `score`          DECIMAL(5,1) DEFAULT NULL COMMENT '成绩',
  `gpa`            DECIMAL(3,2) DEFAULT NULL COMMENT '绩点',
  `import_user_id` BIGINT       DEFAULT NULL COMMENT '导入人ID → sys_user.pk_user',
  `import_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
  PRIMARY KEY (`pk_grade`),
  KEY `idx_grade_student` (`student_id`),
  KEY `idx_grade_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生成绩表';

-- 16. 学生奖项表
DROP TABLE IF EXISTS `student_award`;
CREATE TABLE `student_award` (
  `pk_award`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '奖项ID',
  `student_id`        BIGINT       NOT NULL COMMENT '学生ID → student.pk_student',
  `award_name`        VARCHAR(200) NOT NULL COMMENT '奖项名称',
  `award_level`       VARCHAR(50)  DEFAULT NULL COMMENT '奖项级别：国家级/省级/市级/校级/院级',
  `award_date`        DATE         DEFAULT NULL COMMENT '获奖日期',
  `issuing_authority` VARCHAR(200) DEFAULT NULL COMMENT '颁发机构',
  `description`       VARCHAR(500) DEFAULT NULL COMMENT '奖项描述',
  `import_user_id`    BIGINT       DEFAULT NULL COMMENT '导入人ID → sys_user.pk_user',
  `import_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
  PRIMARY KEY (`pk_award`),
  KEY `idx_award_student` (`student_id`),
  KEY `idx_award_date` (`award_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生奖项表';

-- =============================================
-- 五、运营支撑模块
-- =============================================

-- 13. 操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `pk_log`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id`        BIGINT       DEFAULT NULL COMMENT '操作用户ID → sys_user.pk_user',
  `operation`      VARCHAR(50)  DEFAULT NULL COMMENT '操作类型',
  `module`         VARCHAR(50)  DEFAULT NULL COMMENT '操作模块',
  `description`    VARCHAR(255) DEFAULT NULL COMMENT '详细描述',
  `request_params` TEXT         DEFAULT NULL COMMENT '请求参数（JSON）',
  `ip`             VARCHAR(45)  DEFAULT NULL COMMENT '操作IP',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`pk_log`),
  KEY `idx_log_user` (`user_id`),
  KEY `idx_log_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 14. 通知提醒表
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `pk_notification` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `target_user_id`  BIGINT       NOT NULL COMMENT '接收用户ID → sys_user.pk_user',
  `sender_id`       BIGINT       DEFAULT NULL COMMENT '发送者ID，系统通知可为空',
  `title`           VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `content`         TEXT         DEFAULT NULL COMMENT '内容',
  `is_read`         TINYINT      DEFAULT 0 COMMENT '已读状态：0-未读 1-已读',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
  PRIMARY KEY (`pk_notification`),
  KEY `idx_notif_target` (`target_user_id`),
  KEY `idx_notif_sender` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知提醒表';

-- 15. 档案导出申请表
DROP TABLE IF EXISTS `archive_export_request`;
CREATE TABLE `archive_export_request` (
  `pk_request`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `student_id`     BIGINT       NOT NULL COMMENT '学生ID → student.pk_student',
  `request_type`   VARCHAR(50)  DEFAULT NULL COMMENT '申请类型，如：档案打印、档案下载',
  `reason`         VARCHAR(500) DEFAULT NULL COMMENT '申请原因',
  `status`         TINYINT      DEFAULT 0 COMMENT '审核状态：0-待审核 1-已通过 2-已驳回',
  `audit_user_id`  BIGINT       DEFAULT NULL COMMENT '审核人ID → sys_user.pk_user',
  `audit_time`     DATETIME     DEFAULT NULL COMMENT '审核时间',
  `audit_remark`   VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `expire_time`    DATETIME     DEFAULT NULL COMMENT '审核通过后授权有效期（默认24小时）',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`pk_request`),
  KEY `idx_export_student` (`student_id`),
  KEY `idx_export_status` (`status`),
  KEY `idx_export_audit_user` (`audit_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案导出申请表';

-- =============================================
-- 六、初始数据
-- =============================================

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 默认角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES
  ('系统管理员', 'admin',           '系统管理员，拥有所有权限'),
  ('学生',       'student',         '学生用户，可查看个人档案信息'),
  ('辅导员',     'counselor',       '辅导员，可管理所负责学生的档案'),
  ('档案管理员', 'archive_manager', '档案管理员，负责档案审核');

-- 默认档案材料分类
INSERT INTO `archive_category` (`category_name`, `sort_order`, `is_required`) VALUES
  ('入学登记表',   1, 1),
  ('成绩单',       2, 1),
  ('奖惩记录',     3, 0),
  ('毕业论文',     4, 0),
  ('实习鉴定表',   5, 0),
  ('体检报告',     6, 0),
  ('学位证书复印件', 7, 0),
  ('其他材料',     99, 0);
