-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
-- ZhuaTech VMS / MySQL 8.x initial schema.
-- 仅用于全新数据库初始化；已有数据库请先备份，不要用本文件替代版本化迁移。
-- 表结构依据 backend/src/main/java/cn/zhuatech/vms/model 下的 JPA 实体整理。

CREATE DATABASE IF NOT EXISTS `zhuatech_vms`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `zhuatech_vms`;

CREATE TABLE IF NOT EXISTS `vms_enterprise_site` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `site_code` VARCHAR(32) NOT NULL,
  `site_name` VARCHAR(80) NOT NULL,
  `address` VARCHAR(160) NOT NULL,
  `timezone` VARCHAR(40) NOT NULL,
  `slot_capacity` INT NOT NULL,
  `assembly_point` VARCHAR(80) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_site_code` (`site_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_site_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(20) NOT NULL,
  `code` VARCHAR(40) NOT NULL,
  `name` VARCHAR(80) NOT NULL,
  `department` VARCHAR(80) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_resource_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_visitor_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `visitor_name` VARCHAR(40) NOT NULL,
  `company` VARCHAR(80) NOT NULL,
  `phone` VARCHAR(30) NOT NULL,
  `identity_verified` TINYINT(1) NOT NULL,
  `blacklisted` TINYINT(1) NOT NULL,
  `visit_count` INT NOT NULL,
  `last_visit_at` DATETIME(6) NULL,
  `note` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_visitor_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_contractor_credential` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `company_name` VARCHAR(100) NOT NULL,
  `credential_type` VARCHAR(40) NOT NULL,
  `credential_no` VARCHAR(60) NOT NULL,
  `valid_until` DATE NOT NULL,
  `safety_training_completed` TINYINT(1) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_credential_no` (`credential_no`),
  KEY `idx_vms_contractor_company` (`company_name`),
  KEY `idx_vms_contractor_validity` (`status`, `valid_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_appointment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `appointment_no` VARCHAR(40) NOT NULL,
  `visitor_name` VARCHAR(40) NOT NULL,
  `visitor_company` VARCHAR(80) NOT NULL,
  `visitor_phone` VARCHAR(30) NOT NULL,
  `host_name` VARCHAR(40) NOT NULL,
  `purpose` VARCHAR(120) NOT NULL,
  `visit_date` DATE NOT NULL,
  `time_slot` VARCHAR(40) NOT NULL,
  `access_area` VARCHAR(60) NOT NULL,
  `visitor_count` INT NOT NULL,
  `status` VARCHAR(24) NOT NULL,
  `risk_level` VARCHAR(16) NOT NULL,
  `site_code` VARCHAR(32) NULL,
  `client_request_id` VARCHAR(64) NULL,
  `approval_stage` VARCHAR(24) NULL,
  `pass_code` VARCHAR(32) NULL,
  `checked_in_at` DATETIME(6) NULL,
  `checked_out_at` DATETIME(6) NULL,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_appointment_no` (`appointment_no`),
  UNIQUE KEY `uk_vms_client_request_id` (`client_request_id`),
  KEY `idx_vms_appointment_status_date` (`status`, `visit_date`),
  KEY `idx_vms_appointment_site_slot` (`site_code`, `visit_date`, `time_slot`),
  KEY `idx_vms_appointment_updated` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_approval_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `appointment_no` VARCHAR(40) NOT NULL,
  `stage` VARCHAR(24) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `assignee` VARCHAR(60) NOT NULL,
  `decision_by` VARCHAR(60) NULL,
  `comment` VARCHAR(200) NULL,
  `due_at` DATETIME(6) NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `decided_at` DATETIME(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_vms_approval_business_status` (`appointment_no`, `status`),
  KEY `idx_vms_approval_due` (`status`, `due_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_appointment_document` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `appointment_no` VARCHAR(40) NOT NULL,
  `document_type` VARCHAR(40) NOT NULL,
  `file_name` VARCHAR(120) NOT NULL,
  `checksum` VARCHAR(64) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `submitted_at` DATETIME(6) NOT NULL,
  `reviewed_at` DATETIME(6) NULL,
  `reviewed_by` VARCHAR(40) NULL,
  `review_comment` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_document_type` (`appointment_no`, `document_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_visitor_badge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `badge_no` VARCHAR(32) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `appointment_no` VARCHAR(40) NULL,
  `holder_name` VARCHAR(40) NULL,
  `issued_at` DATETIME(6) NULL,
  `returned_at` DATETIME(6) NULL,
  `remark` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_badge_no` (`badge_no`),
  KEY `idx_vms_badge_status` (`status`),
  KEY `idx_vms_badge_appointment` (`appointment_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_gate_access_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `event_no` VARCHAR(40) NOT NULL,
  `appointment_no` VARCHAR(40) NOT NULL,
  `gate_code` VARCHAR(40) NOT NULL,
  `direction` VARCHAR(8) NOT NULL,
  `result` VARCHAR(12) NOT NULL,
  `reason` VARCHAR(160) NOT NULL,
  `operator_name` VARCHAR(40) NOT NULL,
  `occurred_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_access_event_no` (`event_no`),
  KEY `idx_vms_access_appointment_time` (`appointment_no`, `occurred_at`),
  KEY `idx_vms_access_gate_time` (`gate_code`, `occurred_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_risk_alert` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `alert_no` VARCHAR(40) NOT NULL,
  `type` VARCHAR(30) NOT NULL,
  `level` VARCHAR(16) NOT NULL,
  `title` VARCHAR(120) NOT NULL,
  `related_no` VARCHAR(40) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `assignee` VARCHAR(40) NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `handled_at` DATETIME(6) NULL,
  `resolution` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_alert_no` (`alert_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_notification_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `version` BIGINT NULL,
  `reference_no` VARCHAR(40) NOT NULL,
  `channel` VARCHAR(30) NOT NULL,
  `recipient` VARCHAR(80) NOT NULL,
  `template_code` VARCHAR(40) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `attempts` INT NOT NULL,
  `last_error` VARCHAR(200) NULL,
  `next_retry_at` DATETIME(6) NULL,
  `created_at` DATETIME(6) NOT NULL,
  `sent_at` DATETIME(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_vms_notice_status_retry` (`status`, `next_retry_at`),
  KEY `idx_vms_notice_reference` (`reference_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `module` VARCHAR(30) NOT NULL,
  `action` VARCHAR(40) NOT NULL,
  `business_no` VARCHAR(60) NOT NULL,
  `operator_name` VARCHAR(40) NOT NULL,
  `detail` VARCHAR(240) NOT NULL,
  `occurred_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vms_system_setting` (
  `setting_key` VARCHAR(60) NOT NULL,
  `setting_value` VARCHAR(300) NOT NULL,
  PRIMARY KEY (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `work_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `record_no` VARCHAR(40) NOT NULL,
  `title` VARCHAR(120) NOT NULL,
  `status` VARCHAR(24) NOT NULL,
  `owner` VARCHAR(40) NOT NULL,
  `priority` VARCHAR(16) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vms_work_item_record_no` (`record_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
