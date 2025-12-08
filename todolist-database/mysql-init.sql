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
    `receive_email`     BOOLEAN NOT NULL,
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL 
);

CREATE TABLE `personal_label` (
    `id` CHAR(36) PRIMARY KEY,
    `account_id` CHAR(36) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`)
);


CREATE TABLE `project` (
    `id`                CHAR(36) PRIMARY KEY,
    `name`              VARCHAR(255) NOT NULL,
    `is_archived`       BOOLEAN NOT NULL,
    
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL,
    `is_default`        BOOLEAN NOT NULL
);

CREATE TABLE `project_label` (
    `id` CHAR(36) PRIMARY KEY,
    `project_id` CHAR(36) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `created_by` CHAR(36),
    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`),
    FOREIGN KEY (`created_by`) REFERENCES `account`(`id`)
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
    `status`            ENUM('PENDING','ACCEPTED','DECLINED') NOT NULL,

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
    `priority`          ENUM('CRITICAL','HIGH', 'MEDIUM', 'LOW') DEFAULT NULL,
    `deadline`          TIMESTAMP,
    `start_time`        TIMESTAMP,
    `completed_at`      TIMESTAMP,
    `task_father_id`    CHAR(36),             
    `account_id`        CHAR(36),   
    `created_at`        TIMESTAMP NOT NULL,
    `updated_at`        TIMESTAMP NOT NULL,
    `deleted_at`        TIMESTAMP,
    `is_deleted`        BOOLEAN NOT NULL,
    `created_by`        CHAR(36),  

    FOREIGN KEY (`section_id`) REFERENCES `section`(`id`),
    FOREIGN KEY (`task_father_id`) REFERENCES `task`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`),
    FOREIGN KEY (`created_by`) REFERENCES `account`(`id`)
);

CREATE TABLE `task_label` (
    `id` CHAR(36) PRIMARY KEY,  -- primary key riêng
    `task_id` CHAR(36) NOT NULL,
    `label_id` CHAR(36) NOT NULL,
    `is_ai_generated` BOOLEAN DEFAULT FALSE,
    `confidence` FLOAT DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL,

    FOREIGN KEY (`task_id`) REFERENCES `task`(`id`)
    -- label_id liên kết với personal_label hoặc project_label tùy label_type
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
INSERT INTO `account` (`id`, `email`, `password`, `avatar`, `display_name`, `role`, `status`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`,`receive_email`)
VALUES
('11111111-1111-1111-1111-111111111111', 'admin@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'Admin User', 'ADMIN', 'ACTIVE', NOW(), NOW(), NULL, 0, 1),
('22222222-2222-2222-2222-222222222222', 'user1@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User One', 'USER', 'ACTIVE', NOW(), NOW(), NULL, 0, 1),
('33333333-3333-3333-3333-333333333333', 'user2@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Two', 'USER', 'ACTIVE', NOW(), NOW(), NULL, 0, 0),
('0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'user3@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Four', 'USER', 'INACTIVE', NOW(), NOW(), NULL, 0, 1),
('c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'user4@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Five', 'USER', 'ACTIVE', NOW(), NOW(), NULL, 0, 1);

INSERT INTO `personal_label` (`id`, `account_id`, `name`, `description`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('99999999-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Urgent', 'Task cần làm gấp', NOW(), NOW(), NULL, FALSE),
('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'Review', 'Task cần review', NOW(), NOW(), NULL, FALSE),
('33333333-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 'Learning', 'Task học tập, nghiên cứu', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100001', '11111111-1111-1111-1111-111111111111', 'Work', 'Tasks related to work and office.', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100002', '11111111-1111-1111-1111-111111111111', 'Personal', 'Personal activities and tasks.', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100003', '11111111-1111-1111-1111-111111111111', 'Health', 'Health-related reminders.', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100004', '11111111-1111-1111-1111-111111111111', 'Shopping', 'Items to buy or shopping plan.', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100005', '11111111-1111-1111-1111-111111111111', 'Study', 'Learning and study tasks.', NOW(), NOW(), NULL, FALSE);


-- 2. Dữ liệu mẫu cho bảng `project`
INSERT INTO `project` (`id`, `name`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`, `is_default`)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Project Alpha', FALSE, NOW(), NOW(), NULL, FALSE, TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Project Beta', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Project Gamma', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Project Delta', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Project Omega', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Project Phoenix', FALSE, NOW(), NOW(), NULL, FALSE, FALSE);

INSERT INTO `project_label` (`id`, `project_id`, `name`, `description`, `created_by`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Backend', 'Task liên quan backend', '11111111-1111-1111-1111-111111111111', NOW(), NOW(), NULL, FALSE),
('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Database', 'Task liên quan database', '11111111-1111-1111-1111-111111111111', NOW(), NOW(), NULL, FALSE),
('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Frontend', 'Task liên quan frontend', '11111111-1111-1111-1111-111111111111', NOW(), NOW(), NULL, FALSE);


-- 3. Dữ liệu mẫu cho bảng `member`
INSERT INTO `member` (`id`, `project_id`, `account_id`, `role`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`,`status`)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'OWNER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 'OWNER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
-- User3 vào Project Alpha
('11111111-aaaa-aaaa-aaaa-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),

-- User4 vào Project Alpha
('22222222-aaaa-aaaa-aaaa-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),

-- User3 vào Project Beta
('33333333-bbbb-bbbb-bbbb-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),

-- User4 vào Project Beta
('44444444-bbbb-bbbb-bbbb-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED');
-- 4. Dữ liệu mẫu cho bảng `section`
INSERT INTO `section` (`id`, `project_id`, `name`, `position`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'To Do', 1, FALSE, NOW(), NOW(), NULL, FALSE),
('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'In Progress', 2, FALSE, NOW(), NOW(), NULL, FALSE),
('33333333-cccc-cccc-cccc-cccccccccccc', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Backlog', 1, FALSE, NOW(), NOW(), NULL, FALSE),
-- Thêm section cho Project Alpha
('44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Review', 3, FALSE, NOW(), NOW(), NULL, FALSE),
('55555555-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Done', 4, FALSE, NOW(), NOW(), NULL, FALSE),

-- Thêm section cho Project Beta
('66666666-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'In Progress', 2, FALSE, NOW(), NOW(), NULL, FALSE),
('77777777-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Done', 3, FALSE, NOW(), NOW(), NULL, FALSE);

-- 5. Dữ liệu mẫu cho bảng `task`
INSERT INTO `task` (`id`, `section_id`, `title`, `description`, `is_archived`, `is_pinned`, `status`, `priority`, `deadline`, `start_time`, `completed_at`, `task_father_id`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`, `account_id`, `created_by`)
VALUES
('44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Setup Database', 'Thiết lập cơ sở dữ liệu cho project', FALSE, FALSE, 'PENDING', 'HIGH', '2025-09-30 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('55555555-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Design Schema', 'Thiết kế các bảng cho hệ thống', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-05 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('66666666-cccc-cccc-cccc-cccccccccccc', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Implement API', 'Tạo API cho module project', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-10 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),-- Project Alpha - To Do
('77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Setup Backend', 'Cài đặt môi trường backend cho project', FALSE, FALSE, 'PENDING', 'HIGH', '2025-09-25 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('88888888-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Frontend Skeleton', 'Tạo cấu trúc cơ bản cho frontend', FALSE, FALSE, 'PENDING', 'MEDIUM', '2025-09-27 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
-- Project Alpha - In Progress
('99999999-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Create REST API', 'Tạo các endpoint chính cho project', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-02 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Unit Test API', 'Viết unit test cho API đã tạo', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-04 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),

-- Project Alpha - Review
('bbbbbbbb-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Code Review Backend', 'Đánh giá code backend trước khi merge', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-06 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),

-- Project Alpha - Done
('cccccccc-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Deploy App', 'Triển khai ứng dụng lên server', FALSE, FALSE, 'COMPLETED', 'HIGH', '2025-09-30 23:59:59', NOW(), NOW(), NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),

-- Project Beta - Backlog
('dddddddd-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-cccc-cccc-cccc-cccccccccccc', 'Research New Feature', 'Nghiên cứu tính năng mới cho project Beta', FALSE, FALSE, 'PENDING', 'LOW', '2025-10-15 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),

-- Project Beta - In Progress
('eeeeeeee-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '66666666-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Develop Module X', 'Phát triển module X cho project Beta', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-12 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),

-- Project Beta - Done
('ffffffff-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '77777777-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Finalize Documentation', 'Hoàn thiện tài liệu dự án', FALSE, FALSE, 'COMPLETED', 'MEDIUM', '2025-10-10 23:59:59', NOW(), NOW(), NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111');

INSERT INTO `task_label` (`id`, `task_id`, `label_id`, `is_ai_generated`, `confidence`, `created_at`)
VALUES
('aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb',  TRUE, 0.95, NOW()),
('aaaa2222-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  TRUE, 0.9, NOW()),
('aaaa3333-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb',  TRUE, 0.85, NOW()),
('aaaa4444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  TRUE, 0.9, NOW()),
('aaaa5555-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',  TRUE, 0.75, NOW());



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
-- BẢNG NOTIFICATION (ĐÃ ĐỒNG BỘ ENUM)
-- ==========================================
CREATE TABLE `notification` (
 	`id` 			 CHAR(36) PRIMARY KEY,
 	`receiver_id` 	 CHAR(36) NOT NULL, 		-- người nhận thông báo
 	`actor_id` 		 CHAR(36), 				    -- người thực hiện hành động
 	`project_id` 	 CHAR(36), 				    -- nếu thông báo liên quan tới project
 	`task_id` 		 CHAR(36), 				    -- nếu liên quan tới task
 	`type` ENUM(
		'PROJECT_MEMBER_ADDED',
		'PROJECT_MEMBER_ROLE_UPDATED', -- Thay thế cho PROJECT_MEMBER_JOINED/ADDED cũ
        
		'TASK_ASSIGNED',
		'TASK_COMMENTED',
		'TASK_UPDATED',
		'TASK_COMPLETED',
		'TASK_REOPENED',
		'TASK_DUE_SOON',
		'TASK_OVERDUE',
        'REQUEST_ACCEPTED',
        'REQUEST_DECLINED',
		'PROJECT_DELETED'
	) NOT NULL,
 	`title` 		 VARCHAR(255) NOT NULL,
 	`content` 		 TEXT NOT NULL,
 	`is_read` 		 BOOLEAN NOT NULL DEFAULT FALSE,
 	`read_at` 		 TIMESTAMP NULL,
 
 	`created_at` 	 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 	`deleted_at` 	 TIMESTAMP NULL,
 	`is_deleted` 	 BOOLEAN NOT NULL DEFAULT FALSE,
 
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
 'PROJECT_MEMBER_ADDED', -- Đã đồng bộ
 'Bạn được thêm vào Project Alpha',
 'Admin User đã thêm bạn vào Project Alpha với vai trò MEMBER.',
 FALSE, NOW(), FALSE);

-- Khi Admin comment vào task
-- Khi quyền của User Three được CẬP NHẬT trong Project Alpha
INSERT INTO `notification`
(`id`, `receiver_id`, `actor_id`, `project_id`, `type`, `title`, `content`, `is_read`, `created_at`, `is_deleted`)
VALUES
('cccc3333-3333-3333-3333-333333333333', 
 '33333333-3333-3333-3333-333333333333', -- User Three là người nhận
 '11111111-1111-1111-1111-111111111111', -- Admin là người thực hiện
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'PROJECT_MEMBER_ROLE_UPDATED', -- ENUM mới
 'Quyền của bạn đã được cập nhật',
 'Admin User vừa cập nhật quyền của bạn thành QUẢN LÝ trong Project Alpha.',
 FALSE, NOW(), FALSE);
