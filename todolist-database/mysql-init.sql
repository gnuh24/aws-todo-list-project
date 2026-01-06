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
    
        -- ==== TWO FACTOR AUTHENTICATION ====
    `two_factor_enabled`       BOOLEAN NOT NULL DEFAULT FALSE,
    `two_factor_secret`        VARCHAR(64),
    `two_factor_verified_at`   TIMESTAMP NULL,
    
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
    `role`              ENUM('OWNER', 'MEMBER') NOT NULL,

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
('0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'user3@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Three', 'USER', 'INACTIVE', NOW(), NOW(), NULL, 0, 1),
('c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'user4@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Four', 'USER', 'BANNED', NOW(), NOW(), NULL, 0, 1);
-- ('c5f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'user5@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Five', 'USER', 'DELETED', NOW(), NOW(), NOW(), 1, 1);

-- 2. Dữ liệu mẫu cho bảng `personal_label`
INSERT INTO `personal_label` (`id`, `account_id`, `name`, `description`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('99999999-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Urgent', 'Tasks that need to be done immediately', NOW(), NOW(), NULL, FALSE),
('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'Review', 'Tasks that need to be reviewed', NOW(), NOW(), NULL, FALSE),
('33333333-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 'Learning', 'Tasks for learning and research', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100001', '11111111-1111-1111-1111-111111111111', 'Work', 'Tasks related to work and office', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100002', '11111111-1111-1111-1111-111111111111', 'Personal', 'Personal activities and tasks', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100003', '11111111-1111-1111-1111-111111111111', 'Health', 'Health-related reminders', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100004', '11111111-1111-1111-1111-111111111111', 'Shopping', 'Items to buy or shopping plan', NOW(), NOW(), NULL, FALSE),
('a1fb2c53-0c12-4e93-9d30-bf0b1b100005', '11111111-1111-1111-1111-111111111111', 'Study', 'Learning and study tasks', NOW(), NOW(), NULL, FALSE);

-- 3. Dữ liệu mẫu cho bảng `project`
INSERT INTO `project` (`id`, `name`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`, `is_default`)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Project Alpha', FALSE, NOW(), NOW(), NULL, FALSE, TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Project Beta', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Project Gamma', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Project Delta', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Project Omega', FALSE, NOW(), NOW(), NULL, FALSE, FALSE),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Project Phoenix', FALSE, NOW(), NOW(), NULL, FALSE, FALSE);

-- 4. Dữ liệu mẫu cho bảng `member`
INSERT INTO `member` (`id`, `project_id`, `account_id`, `role`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`,`status`)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'OWNER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 'OWNER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('11111111-aaaa-aaaa-aaaa-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('22222222-aaaa-aaaa-aaaa-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('33333333-bbbb-bbbb-bbbb-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED'),
('44444444-bbbb-bbbb-bbbb-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'MEMBER', NOW(), NOW(), NULL, FALSE, 'ACCEPTED');

-- 5. Dữ liệu mẫu cho bảng `section`
INSERT INTO `section` (`id`, `project_id`, `name`, `position`, `is_archived`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'To Do', 1, FALSE, NOW(), NOW(), NULL, FALSE),
('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'In Progress', 2, FALSE, NOW(), NOW(), NULL, FALSE),
('33333333-cccc-cccc-cccc-cccccccccccc', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Backlog', 1, FALSE, NOW(), NOW(), NULL, FALSE),
('44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Review', 3, FALSE, NOW(), NOW(), NULL, FALSE),
('55555555-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Done', 4, FALSE, NOW(), NOW(), NULL, FALSE),
('66666666-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'In Progress', 2, FALSE, NOW(), NOW(), NULL, FALSE),
('77777777-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Done', 3, FALSE, NOW(), NOW(), NULL, FALSE);

-- 6. Dữ liệu mẫu cho bảng `task`
INSERT INTO `task` (`id`, `section_id`, `title`, `description`, `is_archived`, `is_pinned`, `status`, `priority`, `deadline`, `start_time`, `completed_at`, `task_father_id`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`, `account_id`, `created_by`)
VALUES
('44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Setup Database', 'Set up the database for the project', FALSE, FALSE, 'PENDING', 'HIGH', '2025-09-30 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('55555555-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Design Schema', 'Design the database tables for the system', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-05 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('66666666-cccc-cccc-cccc-cccccccccccc', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Implement API', 'Develop API for the project module', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-10 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Setup Backend', 'Set up the backend environment for the project', FALSE, FALSE, 'PENDING', 'HIGH', '2025-09-25 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('88888888-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Frontend Skeleton', 'Create the basic frontend structure', FALSE, FALSE, 'PENDING', 'MEDIUM', '2025-09-27 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('99999999-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Create REST API', 'Create main endpoints for the project', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-02 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Unit Test API', 'Write unit tests for the created API', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-04 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('bbbbbbbb-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Backend Code Review', 'Review backend code before merging', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-06 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('cccccccc-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Deploy App', 'Deploy the application to the server', FALSE, FALSE, 'COMPLETED', 'HIGH', '2025-09-30 23:59:59', NOW(), NOW(), NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('dddddddd-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-cccc-cccc-cccc-cccccccccccc', 'Research New Feature', 'Research new features for Project Beta', FALSE, FALSE, 'PENDING', 'LOW', '2025-10-15 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('eeeeeeee-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '66666666-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Develop Module X', 'Develop module X for Project Beta', FALSE, TRUE, 'IN_PROGRESS', 'HIGH', '2025-10-12 23:59:59', NOW(), NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('ffffffff-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '77777777-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Finalize Documentation', 'Finalize project documentation', FALSE, FALSE, 'COMPLETED', 'MEDIUM', '2025-10-10 23:59:59', NOW(), NOW(), NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
-- Additional new tasks
('10101010-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Setup CI/CD', 'Configure continuous integration and deployment', FALSE, FALSE, 'PENDING', 'HIGH', '2025-10-20 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111'),
('20202020-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Create API Documentation', 'Document all API endpoints', FALSE, FALSE, 'READY', 'MEDIUM', '2025-10-22 23:59:59', NULL, NULL, NULL, NOW(), NOW(), NULL, FALSE, NULL, '11111111-1111-1111-1111-111111111111');

-- =====================================================
-- Additional hierarchical tasks (parent / child)
-- =====================================================

INSERT INTO `task`
(`id`, `section_id`, `title`, `description`, `is_archived`, `is_pinned`, `status`,
 `priority`, `deadline`, `start_time`, `completed_at`, `task_father_id`,
 `created_at`, `updated_at`, `deleted_at`, `is_deleted`, `account_id`, `created_by`)
VALUES

-- 1. Parent task: API Security
('11111111-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'API Security',
 'Implement security mechanisms for APIs',
 FALSE, FALSE, 'PENDING', 'HIGH',
 '2025-10-18 23:59:59', NULL, NULL, NULL,
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 2. Child of API Security
('22222222-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'JWT Authentication',
 'Implement JWT authentication for APIs',
 FALSE, FALSE, 'READY', 'HIGH',
 '2025-10-14 23:59:59', NULL, NULL,
 '11111111-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 3. Child of API Security
('33333333-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Role-Based Authorization',
 'Add role-based access control',
 FALSE, FALSE, 'PENDING', 'MEDIUM',
 '2025-10-16 23:59:59', NULL, NULL,
 '11111111-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 4. Child of existing task: Implement API
('44444444-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'API Error Handling',
 'Standardize API error responses',
 FALSE, FALSE, 'IN_PROGRESS', 'MEDIUM',
 '2025-10-11 23:59:59', NOW(), NULL,
 '66666666-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 5. Child of existing task: Setup Backend
('55555555-cccc-cccc-cccc-cccccccccccc',
 '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'Configure Environment Variables',
 'Setup env variables for backend',
 FALSE, FALSE, 'READY', 'LOW',
 '2025-09-28 23:59:59', NULL, NULL,
 '77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 6. Parent task: Frontend Optimization
('66666666-dddd-dddd-dddd-dddddddddddd',
 '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'Frontend Optimization',
 'Optimize frontend performance',
 FALSE, FALSE, 'PENDING', 'MEDIUM',
 '2025-10-22 23:59:59', NULL, NULL, NULL,
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 7. Child of Frontend Optimization
('77777777-dddd-dddd-dddd-dddddddddddd',
 '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'Reduce Bundle Size',
 'Optimize bundle size using code splitting',
 FALSE, FALSE, 'PENDING', 'MEDIUM',
 '2025-10-19 23:59:59', NULL, NULL,
 '66666666-dddd-dddd-dddd-dddddddddddd',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 8. Child of Frontend Optimization
('88888888-dddd-dddd-dddd-dddddddddddd',
 '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'Optimize Images',
 'Compress and optimize images',
 FALSE, FALSE, 'READY', 'LOW',
 '2025-10-18 23:59:59', NULL, NULL,
 '66666666-dddd-dddd-dddd-dddddddddddd',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 9. Child of Develop Module X
('99999999-dddd-dddd-dddd-dddddddddddd',
 '66666666-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Module X Validation',
 'Add input validation for Module X',
 FALSE, FALSE, 'IN_PROGRESS', 'HIGH',
 '2025-10-13 23:59:59', NOW(), NULL,
 'eeeeeeee-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 10. Child of Develop Module X
('aaaaaaaa-dddd-dddd-dddd-dddddddddddd',
 '66666666-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Module X Logging',
 'Implement logging for Module X',
 FALSE, FALSE, 'PENDING', 'MEDIUM',
 '2025-10-14 23:59:59', NULL, NULL,
 'eeeeeeee-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111');


INSERT INTO `task`
(`id`, `section_id`, `title`, `description`, `is_archived`, `is_pinned`, `status`,
 `priority`, `deadline`, `start_time`, `completed_at`, `task_father_id`,
 `created_at`, `updated_at`, `deleted_at`, `is_deleted`, `account_id`, `created_by`)
VALUES

-- 11. Child of JWT Authentication
('aaaa1111-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Access Token Generation',
 'Implement access token generation logic',
 FALSE, FALSE, 'READY', 'HIGH',
 '2025-10-12 23:59:59', NULL, NULL,
 '22222222-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 12. Child of JWT Authentication
('bbbb2222-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Refresh Token Flow',
 'Implement refresh token and rotation mechanism',
 FALSE, FALSE, 'PENDING', 'HIGH',
 '2025-10-13 23:59:59', NULL, NULL,
 '22222222-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 13. Child of Role-Based Authorization
('cccc3333-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Define Roles and Permissions',
 'Define system roles and permission matrix',
 FALSE, FALSE, 'READY', 'MEDIUM',
 '2025-10-14 23:59:59', NULL, NULL,
 '33333333-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111'),

-- 14. Child of Role-Based Authorization
('dddd4444-cccc-cccc-cccc-cccccccccccc',
 '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'Authorization Middleware',
 'Implement permission-check middleware',
 FALSE, FALSE, 'PENDING', 'MEDIUM',
 '2025-10-15 23:59:59', NULL, NULL,
 '33333333-cccc-cccc-cccc-cccccccccccc',
 NOW(), NOW(), NULL, FALSE, NULL,
 '11111111-1111-1111-1111-111111111111');



-- 7. Dữ liệu mẫu cho bảng `task_comment`
INSERT INTO `task_comment` (`id`, `task_id`, `account_id`, `comment`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Database is set up. Please check.', NOW(), NOW(), NULL, FALSE),
('88888888-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'API is under development. Needs further testing.', NOW(), NOW(), NULL, FALSE);

-- =====================================================
-- Seed data for table `task_comment` (50 comments)
-- =====================================================

INSERT INTO `task_comment`
(`id`, `task_id`, `account_id`, `comment`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES

-- Setup Database (10 comments)
('cmt-001', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Initial database schema created.', NOW(), NOW(), NULL, FALSE),
('cmt-002', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'Indexes need optimization.', NOW(), NOW(), NULL, FALSE),
('cmt-003', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Foreign keys look correct.', NOW(), NOW(), NULL, FALSE),
('cmt-004', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'Consider soft delete strategy.', NOW(), NOW(), NULL, FALSE),
('cmt-005', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Added audit fields.', NOW(), NOW(), NULL, FALSE),
('cmt-006', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'Migration script tested.', NOW(), NOW(), NULL, FALSE),
('cmt-007', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Seed data inserted.', NOW(), NOW(), NULL, FALSE),
('cmt-008', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'Performance is acceptable.', NOW(), NOW(), NULL, FALSE),
('cmt-009', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Waiting for backend integration.', NOW(), NOW(), NULL, FALSE),
('cmt-010', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'Ready for review.', NOW(), NOW(), NULL, FALSE),

-- Implement API (15 comments)
('cmt-011', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Base controller implemented.', NOW(), NOW(), NULL, FALSE),
('cmt-012', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Endpoints follow REST standard.', NOW(), NOW(), NULL, FALSE),
('cmt-013', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Validation layer added.', NOW(), NOW(), NULL, FALSE),
('cmt-014', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Error handling still missing.', NOW(), NOW(), NULL, FALSE),
('cmt-015', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Swagger config ready.', NOW(), NOW(), NULL, FALSE),
('cmt-016', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Security not implemented yet.', NOW(), NOW(), NULL, FALSE),
('cmt-017', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'API versioning discussed.', NOW(), NOW(), NULL, FALSE),
('cmt-018', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Need rate limiting.', NOW(), NOW(), NULL, FALSE),
('cmt-019', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Refactor service layer.', NOW(), NOW(), NULL, FALSE),
('cmt-020', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Logging added.', NOW(), NOW(), NULL, FALSE),
('cmt-021', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Unit tests in progress.', NOW(), NOW(), NULL, FALSE),
('cmt-022', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Some tests failing.', NOW(), NOW(), NULL, FALSE),
('cmt-023', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Mock data prepared.', NOW(), NOW(), NULL, FALSE),
('cmt-024', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'API response format confirmed.', NOW(), NOW(), NULL, FALSE),
('cmt-025', '66666666-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Ready for frontend integration.', NOW(), NOW(), NULL, FALSE),

-- API Security (10 comments)
('cmt-026', '11111111-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Security requirements defined.', NOW(), NOW(), NULL, FALSE),
('cmt-027', '11111111-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'JWT chosen as auth method.', NOW(), NOW(), NULL, FALSE),
('cmt-028', '11111111-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Role-based model approved.', NOW(), NOW(), NULL, FALSE),
('cmt-029', '11111111-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Token expiration discussed.', NOW(), NOW(), NULL, FALSE),
('cmt-030', '11111111-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Refresh token flow planned.', NOW(), NOW(), NULL, FALSE),
('cmt-031', '11111111-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Permission matrix drafted.', NOW(), NOW(), NULL, FALSE),
('cmt-032', '11111111-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Middleware design ready.', NOW(), NOW(), NULL, FALSE),
('cmt-033', '11111111-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Edge cases listed.', NOW(), NOW(), NULL, FALSE),
('cmt-034', '11111111-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Security review pending.', NOW(), NOW(), NULL, FALSE),
('cmt-035', '11111111-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Implementation starting.', NOW(), NOW(), NULL, FALSE),

-- Frontend Optimization (10 comments)
('cmt-036', '66666666-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 'Performance audit completed.', NOW(), NOW(), NULL, FALSE),
('cmt-037', '66666666-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'Bundle size too large.', NOW(), NOW(), NULL, FALSE),
('cmt-038', '66666666-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 'Lazy loading suggested.', NOW(), NOW(), NULL, FALSE),
('cmt-039', '66666666-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'Images not optimized.', NOW(), NOW(), NULL, FALSE),
('cmt-040', '66666666-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 'Cache strategy discussed.', NOW(), NOW(), NULL, FALSE),
('cmt-041', '66666666-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'Lighthouse score improved.', NOW(), NOW(), NULL, FALSE),
('cmt-042', '66666666-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 'Code splitting implemented.', NOW(), NOW(), NULL, FALSE),
('cmt-043', '66666666-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'Recheck mobile performance.', NOW(), NOW(), NULL, FALSE),
('cmt-044', '66666666-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 'Final review scheduled.', NOW(), NOW(), NULL, FALSE),
('cmt-045', '66666666-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'Optimization complete.', NOW(), NOW(), NULL, FALSE),

-- JWT Authentication (5 comments)
('cmt-046', '22222222-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'JWT library selected.', NOW(), NOW(), NULL, FALSE),
('cmt-047', '22222222-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Claims structure defined.', NOW(), NOW(), NULL, FALSE),
('cmt-048', '22222222-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Token expiry configurable.', NOW(), NOW(), NULL, FALSE),
('cmt-049', '22222222-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'Refresh token pending.', NOW(), NOW(), NULL, FALSE),
('cmt-050', '22222222-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Ready for testing.', NOW(), NOW(), NULL, FALSE);


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


-- 9. Dữ liệu mẫu cho bảng `notification`
INSERT INTO `notification`
(`id`, `receiver_id`, `actor_id`, `project_id`, `task_id`, `type`, `title`, `content`, `is_read`, `created_at`, `is_deleted`)
VALUES
-- Project role updated
('22222222-bbbb-4bbb-bbbb-222222222222', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, 'PROJECT_MEMBER_ROLE_UPDATED', 'Role updated in Project Alpha', 'User Three role has been changed to ADMIN in Project Alpha.', FALSE, NOW(), FALSE),

-- Task assigned
('33333333-cccc-4ccc-cccc-333333333333', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TASK_ASSIGNED', 'New task assigned', 'You have been assigned the task "Setup Database" in Project Alpha.', FALSE, NOW(), FALSE),

-- Task commented
('44444444-dddd-4ddd-dddd-444444444444', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_COMMENTED', 'New comment on your task', 'User One commented on task "Design Schema".', FALSE, NOW(), FALSE),

-- Task updated
('55555555-eeee-4eee-eeee-555555555555', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'dddddddd-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_UPDATED', 'Task updated', 'Task "Research New Feature" has been updated by User Three.', FALSE, NOW(), FALSE),

-- Task completed
('66666666-ffff-4fff-ffff-666666666666', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ffffffff-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_COMPLETED', 'Task completed', 'Task "Finalize Documentation" has been completed by User Two.', FALSE, NOW(), FALSE),

-- Task reopened
('77777777-aaaa-4aaa-aaaa-777777777777', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TASK_REOPENED', 'Task reopened', 'Task "Setup Backend" has been reopened by User Three.', FALSE, NOW(), FALSE),

-- Task due soon
('88888888-bbbb-4bbb-bbbb-888888888888', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '88888888-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TASK_DUE_SOON', 'Task due soon', 'Task "Frontend Skeleton" is due in 3 days.', FALSE, NOW(), FALSE),

-- Task overdue
('99999999-cccc-4ccc-cccc-999999999999', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'eeeeeeee-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_OVERDUE', 'Task overdue', 'Task "Develop Module X" is overdue.', FALSE, NOW(), FALSE),

-- Request accepted
('aaaaaaaa-dddd-4ddd-dddd-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, 'REQUEST_ACCEPTED', 'Request accepted', 'Your request to join Project Beta has been accepted.', FALSE, NOW(), FALSE),

-- Request declined
('bbbbbbbb-eeee-4eee-eeee-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, 'REQUEST_DECLINED', 'Request declined', 'Your request to join Project Alpha has been declined.', FALSE, NOW(), FALSE),

-- Project deleted
('cccccccc-ffff-4fff-ffff-cccccccccccc', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NULL, 'PROJECT_DELETED', 'Project deleted', 'Project Gamma has been deleted by User Two.', FALSE, NOW(), FALSE);