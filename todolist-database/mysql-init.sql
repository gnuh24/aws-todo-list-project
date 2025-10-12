DROP DATABASE IF EXISTS `aws_todolist_database`;
CREATE DATABASE `aws_todolist_database`;
USE `aws_todolist_database`;

CREATE TABLE `account` (
    `id`                CHAR(36) PRIMARY KEY,          
    `email`             VARCHAR(255) UNIQUE NOT NULL,       
    `password`          VARCHAR(255) NOT NULL,           
    `avatar`            VARCHAR(512),                       
    `display_name`      VARCHAR(255) ,       
    `role`              ENUM('ADMIN', 'USER') NOT NULL,
    `status`            ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL,
    
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL 
);

CREATE TABLE `project` (
    `id`                CHAR(36) PRIMARY KEY,
    `name`              VARCHAR(255) NOT NULL,
    `is_archived`       BOOLEAN NOT NULL,
    
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL
);

CREATE TABLE `member` (
    `id`                CHAR(36) PRIMARY KEY,
    `project_id`        CHAR(36) NOT NULL,
    `account_id`        CHAR(36) NOT NULL,
    `role`              ENUM('OWNER', 'ADMIN', 'MEMBER', 'VIEWER') NOT NULL,
    
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL,
    
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`)
);

-- 1. Bảng `section`
CREATE TABLE `section` (
    `id`                CHAR(36) PRIMARY KEY,
    `project_id`        CHAR(36),                
    `name`              VARCHAR(255) NOT NULL,
    `position`          INT NOT NULL,              
    `is_archived`       BOOLEAN NOT NULL,
    
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL,
    
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`)
);

-- 2. Bảng `task`
CREATE TABLE `task` (
    `id`                CHAR(36) PRIMARY KEY,
    `section_id`        CHAR(36),                 
    `title`             VARCHAR(255) NOT NULL,
    `description`       TEXT,
    `is_archived`       BOOLEAN NOT NULL,
    `is_pinned`         BOOLEAN NOT NULL,
    `status`            ENUM('PENDING', 'READY', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT NULL,
    `priority`          ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT NULL,
    `deadline`          TIMESTAMP,
    `start_time`        TIMESTAMP,
    `completed_at`      TIMESTAMP,
    `task_father_id`    CHAR(36),             

    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL,
    
    FOREIGN KEY (`section_id`) REFERENCES `section`(`id`),
    FOREIGN KEY (`task_father_id`) REFERENCES `task`(`id`)
);

-- 1. Bảng task_comment
CREATE TABLE `task_comment` (
    `id`                CHAR(36) PRIMARY KEY,
    `task_id`           CHAR(36) NOT NULL,
    `account_id`        CHAR(36) NOT NULL,
    `comment`           TEXT NOT NULL,

    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL,

    FOREIGN KEY (`task_id`) REFERENCES `task`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`)
);

-- 2. Bảng comment_attachment
CREATE TABLE `comment_attachment` (
    `id`                CHAR(36) PRIMARY KEY,
    `task_comment_id`   CHAR(36) NOT NULL,
    `attachment_url`    VARCHAR(512) NOT NULL,

    `created_at`        TIMESTAMP NOT NULL,

    FOREIGN KEY (`task_comment_id`) REFERENCES `task_comment`(`id`)
);

-- 1. Dữ liệu mẫu cho bảng `account`
INSERT INTO `account` (`id`, `email`, `password`, `avatar`, `display_name`, `role`, `status`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('11111111-1111-1111-1111-111111111111', 'admin@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'Admin User', 'ADMIN', 'ACTIVE', NOW(), NOW(), NULL, FALSE),
('22222222-2222-2222-2222-222222222222', 'user1@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User One', 'USER', 'ACTIVE', NOW(), NOW(), NULL, FALSE),
('33333333-3333-3333-3333-333333333333', 'user2@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Two', 'USER', 'ACTIVE', NOW(), NOW(), NULL, FALSE);

-- 2. Dữ liệu mẫu cho bảng `project`
INSERT INTO `project` (`id`, `name`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Project Alpha', FALSE, NOW(), NOW(), NULL, FALSE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Project Beta', FALSE, NOW(), NOW(), NULL, FALSE);

-- 3. Dữ liệu mẫu cho bảng `member`
INSERT INTO `member` (`id`, `project_id`, `account_id`, `role`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'OWNER', NOW(), NOW(), NULL, FALSE),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'MEMBER', NOW(), NOW(), NULL, FALSE),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 'OWNER', NOW(), NOW(), NULL, FALSE),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333', 'MEMBER', NOW(), NOW(), NULL, FALSE);

-- 4. Dữ liệu mẫu cho bảng `section`
INSERT INTO `section` (`id`, `project_id`, `name`, `position`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'To Do', 1, FALSE, NOW(), NOW(), NULL, FALSE),
('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'In Progress', 2, FALSE, NOW(), NOW(), NULL, FALSE),
('33333333-cccc-cccc-cccc-cccccccccccc', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Backlog', 1, FALSE, NOW(), NOW(), NULL, FALSE);

-- 5. Dữ liệu mẫu cho bảng `task`
INSERT INTO `task` (`id`, `section_id`, `title`, `description`, `is_archived`, `is_pinned`, `status`, `priority`, `deadline`, `start_time`, `completed_at`, `task_father_id`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Setup Database', 'Thiết lập cơ sở dữ liệu cho project', FALSE, FALSE, 'PENDING', 'HIGH', '2025-09-30 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE),
('55555555-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Design Schema', 'Thiết kế các bảng cho hệ thống', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-05 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE),
('66666666-cccc-cccc-cccc-cccccccccccc', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Implement API', 'Tạo API cho module project', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-10 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE);

-- 6. Dữ liệu mẫu cho bảng `task_comment`
INSERT INTO `task_comment` (`id`, `task_id`, `account_id`, `comment`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Database đã tạo xong, kiểm tra lại đi.', NOW(), NOW(), NULL, FALSE),
('88888888-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'API đang phát triển, cần test thêm.', NOW(), NOW(), NULL, FALSE);

-- 7. Dữ liệu mẫu cho bảng `comment_attachment`
INSERT INTO `comment_attachment` (`id`, `task_comment_id`, `attachment_url`, `created_at`)
VALUES
('99999999-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://example.com/db_schema.png', NOW()),
('aaaaaaaa-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '88888888-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://example.com/api_test.json', NOW());


-- ==========================================
-- BẢNG NOTIFICATION
-- ==========================================
CREATE TABLE `notification` (
    `id`                CHAR(36) PRIMARY KEY,
    `receiver_id`       CHAR(36) NOT NULL,         -- người nhận thông báo
    `actor_id`          CHAR(36),                  -- người thực hiện hành động
    `project_id`        CHAR(36),                  -- nếu thông báo liên quan tới project
    `task_id`           CHAR(36),                  -- nếu liên quan tới task
    `type`              ENUM(
                            'PROJECT_MEMBER_JOINED',
                            'PROJECT_MEMBER_ADDED',
                            'TASK_ASSIGNED',
                            'TASK_COMMENTED',
                            'TASK_UPDATED',
                            'TASK_COMPLETED',
                            'TASK_REOPENED',
                            'TASK_DUE_SOON',
                            'TASK_OVERDUE',
                            'PROJECT_DELETED',
                            'NEW_COMMENT'
                        ) NOT NULL,
    `title`             VARCHAR(255) NOT NULL,
    `content`           TEXT NOT NULL,
    `is_read`           BOOLEAN NOT NULL DEFAULT FALSE,
    `read_at`           TIMESTAMP NULL,

    
    `created_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`        TIMESTAMP NULL,
    `is_deleted`        BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (`receiver_id`) REFERENCES `account`(`id`),
    FOREIGN KEY (`actor_id`) REFERENCES `account`(`id`),
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`),
    FOREIGN KEY (`task_id`) REFERENCES `task`(`id`)
);


-- Khi User One được add vào Project Alpha
INSERT INTO `notification`
(`id`, `receiver_id`, `actor_id`, `project_id`, `type`, `title`, `content`, `is_read`, `created_at`, `is_deleted`)
VALUES
('aaaa1111-1111-1111-1111-111111111111', 
 '22222222-2222-2222-2222-222222222222', 
 '11111111-1111-1111-1111-111111111111',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'PROJECT_MEMBER_ADDED',
 'Bạn được thêm vào Project Alpha',
 'Admin User đã thêm bạn vào Project Alpha với vai trò MEMBER.',
 FALSE, NOW(), FALSE);

-- Khi Admin comment vào task
INSERT INTO `notification`
(`id`, `receiver_id`, `actor_id`, `project_id`, `task_id`, `type`, `title`, `content`, `is_read`, `created_at`, `is_deleted`)
VALUES
('bbbb2222-2222-2222-2222-222222222222',
 '22222222-2222-2222-2222-222222222222',
 '11111111-1111-1111-1111-111111111111',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'TASK_COMMENTED',
 'Bình luận mới trên task “Setup Database”',
 'Admin User đã bình luận: "Database đã tạo xong, kiểm tra lại đi."',
 FALSE, NOW(), FALSE);
