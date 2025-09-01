-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    college VARCHAR(100) COMMENT '学院',
    bio TEXT COMMENT '个人简介',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';
-- 创建公告表
CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary TEXT COMMENT '摘要',
    content VARCHAR(500) COMMENT '内容',
    cover_url VARCHAR(500) COMMENT '封面',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    publish_time DATETIME COMMENT '发布时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表';
-- 创建活动表
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    image_url VARCHAR(500) COMMENT '活动图片',
    text_url VARCHAR(500) COMMENT '活动介绍',
    location VARCHAR(200) COMMENT '活动地点',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-未开始，1-正在进行，2-已结束',
    view_count INT DEFAULT 0 COMMENT '浏览次数'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动表';
-- 创建学习资源表
CREATE TABLE resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源描述',
    category ENUM('software', 'hardware', 'embed', 'ai', 'other') DEFAULT 'other' COMMENT '分类',
    type ENUM('document', 'video', 'project', 'other') DEFAULT 'document' COMMENT '类型：按文件类型分类',
    link_url VARCHAR(500) COMMENT '资源链接',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习资源表';
-- 创建项目表
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '项目标题',
    description TEXT COMMENT '项目简介',
    detailed_description VARCHAR(500) COMMENT '项目描述',
    category ENUM('software', 'hardware', 'embed', 'ai', 'others') DEFAULT 'others' COMMENT '项目分类',
    github_url VARCHAR(500) COMMENT 'GitHub链接',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    star_count INT DEFAULT 0 COMMENT '收藏数',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目表';
-- 创建华为竞赛表
CREATE TABLE competitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '竞赛标题',
    description TEXT COMMENT '竞赛描述',
    status ENUM('upcoming', 'ongoing', 'ending') DEFAULT NULL COMMENT '竞赛状态：upcoming-即将开始，ongoing-进行中，ending-已结束',
    start_time DATETIME COMMENT '开始时间',
    official_link VARCHAR(500) COMMENT '官方链接',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '华为竞赛表';
-- 创建实用工具表
CREATE TABLE tools (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '工具名称',
    description TEXT COMMENT '工具描述',
    category ENUM('develop', 'design', 'efficiency', 'others') DEFAULT 'others' COMMENT '工具分类',
    icon_url VARCHAR(500) COMMENT '工具图标URL',
    tool_url VARCHAR(500) NOT NULL COMMENT '工具链接',
    eye_count INT DEFAULT 0 COMMENT '浏览数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实用工具表';
-- 创建华创推文表
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary TEXT COMMENT '文章摘要',
    cover_image VARCHAR(500) COMMENT '封面图片',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    link_url VARCHAR(500) COMMENT '链接',
    publish_time DATETIME COMMENT '发布时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '华创推文表';
-- 创建师兄师姐说表
CREATE TABLE shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '分享标题',
    content LONGTEXT NOT NULL COMMENT '分享摘要',
    category ENUM('learn', 'job', 'plan', 'others') DEFAULT 'others' COMMENT '分享分类',
    text_url VARCHAR(500) COMMENT '分享内容',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '师兄师姐说表';
-- 用户收藏表
CREATE TABLE user_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    resource_title VARCHAR(200) NOT NULL COMMENT '资源标题',
    resource_url VARCHAR(500) COMMENT '资源链接',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表';