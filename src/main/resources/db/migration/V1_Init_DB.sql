
CREATE TABLE IF NOT EXISTS `users` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `is_moderator` int NOT NULL,
    `reg_time` timestamp NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `code` VARCHAR(255),
    `photo` STRING

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `posts` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `is_active` int NOT NULL,
    `moderation_status` ENUM("NEW", "ACCEPTED","DECLINED") NOT NULL DEFAULT "NEW",
    `time` timestamp NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `text` STRING NOT NULL,
    `view_count` int NOT NULL


)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `posts` ADD `moderator_id` int AFTER `moderation_status`;
ALTER TABLE `posts` ADD `user_id` int NOT NULL AFTER `moderator_id`;

CREATE TABLE IF NOT EXISTS `post_votes` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `time` timestamp NOT NULL,
    `value` int NOT NULL


)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `post_votes` ADD `user_id` int NOT NULL AFTER `id`;
ALTER TABLE `post_votes` ADD `post_id` int NOT NULL AFTER `user_id`;

CREATE TABLE IF NOT EXISTS `tags` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `tag2post` (

     `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `tag2post` ADD `post_id` int NOT NULL AFTER `id`;
ALTER TABLE `tag2post` ADD `tag_id` int NOT NULL AFTER `post_id`;

CREATE TABLE IF NOT EXISTS `post_comments` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `time` timestamp NOT NULL,
    `text` STRING NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `post_comments` ADD `parent_id` int NOT NULL AFTER `id`;
ALTER TABLE `post_comments` ADD `post_id` int NOT NULL AFTER `parent_id`;
ALTER TABLE `post_comments` ADD `user_id` int NOT NULL AFTER `post_id`;

CREATE TABLE IF NOT EXISTS `captcha_codes` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `time` timestamp NOT NULL,
    `code` int NOT NULL,
    `secret_code` int NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `global_settings` (

    `code` VARCHAR(255),
    `name` VARCHAR(255),
    `value` VARCHAR(255)

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

