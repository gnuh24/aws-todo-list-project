DROP DATABASE `aws_todolist_database`;
CREATE DATABASE `aws_todolist_database`;
USE `aws_todolist_database`;

CREATE TABLE `account` (
    `id` 				CHAR(10) PRIMARY KEY,          
    
    `email` 			VARCHAR(255) UNIQUE NOT NULL,       
    `password` 			VARCHAR(255) NOT NULL,           
    `avatar` 			VARCHAR(512),                       
    `display_name` 		VARCHAR(255) NOT NULL,       
    `role` 				ENUM('ADMIN', 'USER') 	NOT NULL,
    `status` 			ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL,
    
    `created_at` 		TIMESTAMP NOT NULL,
    `updated_at` 		TIMESTAMP NOT NULL,
    `deleted_at` 		TIMESTAMP,
    `is_deleted` 		BOOLEAN NOT NULL 
);

CREATE TABLE `project` (
    `id` 				CHAR(10) PRIMARY KEY,
    `name` 				VARCHAR(255) NOT NULL,
    `is_archived` 		BOOLEAN NOT NULL,
    
    `created_at` 		TIMESTAMP NOT NULL,
    `updated_at` 		TIMESTAMP NOT NULL,
    `deleted_at` 		TIMESTAMP,
    `is_deleted` 		BOOLEAN NOT NULL
);

CREATE TABLE `member` (
    `id` 				CHAR(10) PRIMARY KEY,
    `project_id` 		CHAR(10) NOT NULL,
    `account_id` 		CHAR(10) NOT NULL,
    `role` 				ENUM('OWNER', 'ADMIN', 'MEMBER', 'VIEWER') NOT NULL,
    
    `created_at` 		TIMESTAMP NOT NULL,
    `updated_at` 		TIMESTAMP NOT NULL,
    `deleted_at` 		TIMESTAMP,
    `is_deleted` 		BOOLEAN NOT NULL,
    
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`)
);

-- 1. Bảng `section`
CREATE TABLE `section` (
    `id` 				CHAR(10) PRIMARY KEY,
    `project_id` 		CHAR(10),                -- Section có thể không thuộc project
    `name` 				VARCHAR(255) NOT NULL,
    `position` 			INT NOT NULL,              -- Thứ tự sắp xếp trong project
    `is_archived` 		BOOLEAN NOT NULL,
    
    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL,
    
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`)
);

-- 2. Bảng `task` cập nhật
CREATE TABLE `task` (
    `id` 				CHAR(10) PRIMARY KEY,
    `section_id` 		CHAR(10),                 -- Task có thể không thuộc section
    `title` 			VARCHAR(255) NOT NULL,
    `description` 		TEXT,
    `is_archived` 		BOOLEAN NOT NULL,
    `is_pinned` 		BOOLEAN NOT NULL,
    `status` 			ENUM('PENDING', 'READY', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT NULL,
    `priority` 			ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT NULL,
    `deadline` 			TIMESTAMP,
    `start_time` 		TIMESTAMP,
    `completed_at` 		TIMESTAMP,
    `task_father_id` 	CHAR(10),             -- Subtask nếu có

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL,
    
    FOREIGN KEY (`section_id`) REFERENCES `section`(`id`),
    FOREIGN KEY (`task_father_id`) REFERENCES `task`(`id`)
);

-- 1. Bảng task_comment
CREATE TABLE `task_comment` (
    `id` 				CHAR(10) PRIMARY KEY,
    `task_id` 			CHAR(10) NOT NULL,
    `account_id` 		CHAR(10) NOT NULL,
    `comment` 			TEXT NOT NULL,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL,

    FOREIGN KEY (`task_id`) REFERENCES `task`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`)
);

-- 2. Bảng comment_attachment
CREATE TABLE `comment_attachment` (
    `id` 				CHAR(10) PRIMARY KEY,
    `task_comment_id` 	CHAR(10) NOT NULL,
    `attachment_url` 	VARCHAR(512) NOT NULL,

    `created_at` TIMESTAMP NOT NULL,

    FOREIGN KEY (`task_comment_id`) REFERENCES `task_comment`(`id`)
);

-- 1. Dữ liệu mẫu cho bảng `account`
INSERT INTO `account` (`id`, `email`, `password`, `avatar`, `display_name`, `role`, `status`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('A000000001', 'admin@gmail.com', 'admin_password', NULL, 'Admin User', 'ADMIN', 'ACTIVE', NOW(), NOW(), NULL, FALSE),
('A000000002', 'user1@gmail.com', 'user1_password', NULL, 'User One', 'USER', 'ACTIVE', NOW(), NOW(), NULL, FALSE),
('A000000003', 'user2@gmail.com', 'user2_password', NULL, 'User Two', 'USER', 'ACTIVE', NOW(), NOW(), NULL, FALSE);

-- 2. Dữ liệu mẫu cho bảng `project`
INSERT INTO `project` (`id`, `name`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('P00000001', 'Project Alpha', FALSE, NOW(), NOW(), NULL, FALSE),
('P00000002', 'Project Beta', FALSE, NOW(), NOW(), NULL, FALSE);

-- 3. Dữ liệu mẫu cho bảng `member`
INSERT INTO `member` (`id`, `project_id`, `account_id`, `role`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('M00000001', 'P00000001', 'A000000001', 'OWNER', NOW(), NOW(), NULL, FALSE),
('M00000002', 'P00000001', 'A000000002', 'MEMBER', NOW(), NOW(), NULL, FALSE),
('M00000003', 'P00000002', 'A000000001', 'OWNER', NOW(), NOW(), NULL, FALSE),
('M00000004', 'P00000002', 'A000000003', 'MEMBER', NOW(), NOW(), NULL, FALSE);

-- 4. Dữ liệu mẫu cho bảng `section`
INSERT INTO `section` (`id`, `project_id`, `name`, `position`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('S00000001', 'P00000001', 'To Do', 1, FALSE, NOW(), NOW(), NULL, FALSE),
('S00000002', 'P00000001', 'In Progress', 2, FALSE, NOW(), NOW(), NULL, FALSE),
('S00000003', 'P00000002', 'Backlog', 1, FALSE, NOW(), NOW(), NULL, FALSE);

-- 5. Dữ liệu mẫu cho bảng `task`
INSERT INTO `task` (`id`, `section_id`, `title`, `description`, `is_archived`, `is_pinned`, `status`, `priority`, `deadline`, `start_time`, `completed_at`, `task_father_id`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('T00000001', 'S00000001', 'Setup Database', 'Thiết lập cơ sở dữ liệu cho project', FALSE, FALSE, 'PENDING', 'HIGH', '2025-09-30 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE),
('T00000002', 'S00000001', 'Design Schema', 'Thiết kế các bảng cho hệ thống', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-05 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE),
('T00000003', 'S00000002', 'Implement API', 'Tạo API cho module project', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-10 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE);

-- 6. Dữ liệu mẫu cho bảng `task_comment`
INSERT INTO `task_comment` (`id`, `task_id`, `account_id`, `comment`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('C00000001', 'T00000001', 'A000000001', 'Database đã tạo xong, kiểm tra lại đi.', NOW(), NOW(), NULL, FALSE),
('C00000002', 'T00000003', 'A000000002', 'API đang phát triển, cần test thêm.', NOW(), NOW(), NULL, FALSE);

-- 7. Dữ liệu mẫu cho bảng `comment_attachment`
INSERT INTO `comment_attachment` (`id`, `task_comment_id`, `attachment_url`, `created_at`)
VALUES
('CA0000001', 'C00000001', 'https://example.com/db_schema.png', NOW()),
('CA0000002', 'C00000002', 'https://example.com/api_test.json', NOW());

