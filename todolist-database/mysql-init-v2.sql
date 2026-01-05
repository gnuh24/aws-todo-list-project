-- __________________________________________________________________ AUTH DATABASE _____________________________________________________________________________

DROP DATABASE IF EXISTS `auth_database`;
CREATE DATABASE `auth_database`;
USE `auth_database`;

CREATE TABLE `account` (
    `id` CHAR(36) PRIMARY KEY,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,

    `avatar` VARCHAR(512),
    `display_name` VARCHAR(255),
    `receive_email` BOOLEAN NOT NULL,

    `role` ENUM('ADMIN', 'USER') NOT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL,

    `two_factor_enabled` BOOLEAN NOT NULL DEFAULT FALSE,
    `two_factor_secret` VARCHAR(64),
    `two_factor_verified_at` TIMESTAMP NULL,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);


INSERT INTO `account` (`id`, `email`, `password`, `avatar`, `display_name`, `role`, `status`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`,`receive_email`)
VALUES
('11111111-1111-1111-1111-111111111111', 'admin@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'Admin User', 'ADMIN', 'ACTIVE', NOW(), NOW(), NULL, 0, 1),
('22222222-2222-2222-2222-222222222222', 'user1@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User One', 'USER', 'ACTIVE', NOW(), NOW(), NULL, 0, 1),
('33333333-3333-3333-3333-333333333333', 'user2@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Two', 'USER', 'ACTIVE', NOW(), NOW(), NULL, 0, 0),
('0c78b06c-9597-4b88-b5aa-f3c13dbf1a44', 'user3@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Three', 'USER', 'INACTIVE', NOW(), NOW(), NULL, 0, 1),
('c4f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'user4@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Four', 'USER', 'BANNED', NOW(), NOW(), NULL, 0, 1);
-- ('c5f6d12b-5e84-4d1a-8ca4-421e83a4e555', 'user5@gmail.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUravfLpyIFi', NULL, 'User Five', 'USER', 'DELETED', NOW(), NOW(), NOW(), 1, 1);


CREATE TABLE `account_recovery_key` (
    `id` CHAR(36) PRIMARY KEY,

    `account_id` CHAR(36) NOT NULL,

    `key_hash` VARCHAR(255) NOT NULL,

    `is_used` TINYINT(1) NOT NULL DEFAULT 0,

    `used_at` TIMESTAMP NULL,

    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT `fk_recovery_key_account`
        FOREIGN KEY (`account_id`)
        REFERENCES `account`(`id`)
        ON DELETE CASCADE,

    UNIQUE (`key_hash`)
);



-- __________________________________________________________________ PROJECT DATABASE _____________________________________________________________________________

DROP DATABASE IF EXISTS `project_database`;
CREATE DATABASE `project_database`;
USE `project_database`;

CREATE TABLE `project` (
    `id` CHAR(36) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `is_archived` BOOLEAN NOT NULL,
    `is_default` BOOLEAN NOT NULL,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);



CREATE TABLE `member` (
    `id` CHAR(36) PRIMARY KEY,
    `project_id` CHAR(36) NOT NULL,
    `account_id` CHAR(36) NOT NULL,

    `role` ENUM('OWNER', 'ADMIN', 'MEMBER', 'VIEWER') NOT NULL,
    `status` ENUM('PENDING', 'ACCEPTED', 'DECLINED') NOT NULL,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `section` (
    `id` CHAR(36) PRIMARY KEY,
    `project_id` CHAR(36) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `position` INT NOT NULL,
    `is_archived` BOOLEAN NOT NULL,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);


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

-- __________________________________________________________________ TASK DATABASE _____________________________________________________________________________
DROP DATABASE IF EXISTS `task_database`;
CREATE DATABASE `task_database`;
USE `task_database`;

CREATE TABLE `personal_label` (
    `id` CHAR(36) PRIMARY KEY,
    `account_id` CHAR(36) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `task` (
    `id` CHAR(36) PRIMARY KEY,

    `section_id` CHAR(36) NOT NULL,

    `title` VARCHAR(255) NOT NULL,
    `description` TEXT,

    `status` ENUM('PENDING','READY','IN_PROGRESS','COMPLETED','CANCELLED'),
    `priority` ENUM('CRITICAL','HIGH','MEDIUM','LOW'),

    `is_archived` BOOLEAN NOT NULL,
    `is_pinned` BOOLEAN NOT NULL,

    `deadline` TIMESTAMP,
    `start_time` TIMESTAMP,
    `completed_at` TIMESTAMP,
	`task_father_id`    CHAR(36), 
     `account_id`        CHAR(36),  
    `created_by` CHAR(36),

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
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
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `task_label` (
    `id` CHAR(36) PRIMARY KEY,
    `task_id` CHAR(36) NOT NULL,
    `label_id` CHAR(36) NOT NULL,
    `label_type` ENUM('PERSONAL','PROJECT') NOT NULL,

    `is_ai_generated` BOOLEAN DEFAULT FALSE,
    `confidence` FLOAT DEFAULT NULL,

    `created_at` TIMESTAMP NOT NULL
);

CREATE TABLE `task_comment` (
    `id` CHAR(36) PRIMARY KEY,
    `task_id` CHAR(36) NOT NULL,
    `account_id` CHAR(36) NOT NULL,
    `comment` TEXT NOT NULL,

    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `comment_attachment` (
    `id` CHAR(36) PRIMARY KEY,
    `task_comment_id` CHAR(36) NOT NULL,
    `attachment_url` VARCHAR(512) NOT NULL,

    `created_at` TIMESTAMP NOT NULL
);


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
-- 7. Dữ liệu mẫu cho bảng `task_comment`
INSERT INTO `task_comment` (`id`, `task_id`, `account_id`, `comment`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`)
VALUES
('77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Database is set up. Please check.', NOW(), NOW(), NULL, FALSE),
('88888888-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '66666666-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'API is under development. Needs further testing.', NOW(), NOW(), NULL, FALSE);

-- 8. Dữ liệu mẫu cho bảng `comment_attachment`
INSERT INTO `comment_attachment` (`id`, `task_comment_id`, `attachment_url`, `created_at`)
VALUES
('99999999-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://example.com/db_schema.png', NOW()),
('aaaaaaaa-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '88888888-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://example.com/api_test.json', NOW());



-- __________________________________________________________________ NOTIFICATION DATABASE _____________________________________________________________________________

DROP DATABASE IF EXISTS `notification_database`;
CREATE DATABASE `notification_database`;
USE `notification_database`;

CREATE TABLE `notification` (
    `id` CHAR(36) PRIMARY KEY,

    `receiver_id` CHAR(36) NOT NULL,
    `actor_id` CHAR(36),

    `project_id` CHAR(36),
    `task_id` CHAR(36),

    `type` ENUM(
        'PROJECT_MEMBER_ADDED',
        'PROJECT_MEMBER_ROLE_UPDATED',

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

    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,

    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `read_at` TIMESTAMP NULL,

    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` TIMESTAMP,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
);


INSERT INTO `notification`
(`id`, `receiver_id`, `actor_id`, `project_id`, `task_id`, `type`, `title`, `content`, `is_read`, `created_at`, `is_deleted`)
VALUES 
('22222222-bbbb-4bbb-bbbb-222222222222', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, 'PROJECT_MEMBER_ROLE_UPDATED', 'Role updated in Project Alpha', 'User Three role has been changed to ADMIN in Project Alpha.', FALSE, NOW(), FALSE),
('33333333-cccc-4ccc-cccc-333333333333', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TASK_ASSIGNED', 'New task assigned', 'You have been assigned the task "Setup Database" in Project Alpha.', FALSE, NOW(), FALSE),
('44444444-dddd-4ddd-dddd-444444444444', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_COMMENTED', 'New comment on your task', 'User One commented on task "Design Schema".', FALSE, NOW(), FALSE),
('55555555-eeee-4eee-eeee-555555555555', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'dddddddd-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_UPDATED', 'Task updated', 'Task "Research New Feature" has been updated by User Three.', FALSE, NOW(), FALSE),
('66666666-ffff-4fff-ffff-666666666666', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ffffffff-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_COMPLETED', 'Task completed', 'Task "Finalize Documentation" has been completed by User Two.', FALSE, NOW(), FALSE),
('77777777-aaaa-4aaa-aaaa-777777777777', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TASK_REOPENED', 'Task reopened', 'Task "Setup Backend" has been reopened by User Three.', FALSE, NOW(), FALSE),
('88888888-bbbb-4bbb-bbbb-888888888888', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '88888888-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TASK_DUE_SOON', 'Task due soon', 'Task "Frontend Skeleton" is due in 3 days.', FALSE, NOW(), FALSE),
('99999999-cccc-4ccc-cccc-999999999999', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'eeeeeeee-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TASK_OVERDUE', 'Task overdue', 'Task "Develop Module X" is overdue.', FALSE, NOW(), FALSE),
('aaaaaaaa-dddd-4ddd-dddd-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, 'REQUEST_ACCEPTED', 'Request accepted', 'Your request to join Project Beta has been accepted.', FALSE, NOW(), FALSE),
('bbbbbbbb-eeee-4eee-eeee-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, 'REQUEST_DECLINED', 'Request declined', 'Your request to join Project Alpha has been declined.', FALSE, NOW(), FALSE),
('cccccccc-ffff-4fff-ffff-cccccccccccc', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NULL, 'PROJECT_DELETED', 'Project deleted', 'Project Gamma has been deleted by User Two.', FALSE, NOW(), FALSE);