
CREATE TABLE `users` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `is_moderator` int NOT NULL,
    `reg_time` DATETIME NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `code` VARCHAR(255),
    `photo` TEXT

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `posts` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `is_active` int NOT NULL,
    `moderation_status` ENUM('NEW', 'ACCEPTED','DECLINED') NOT NULL DEFAULT 'NEW',
    `moderator_id` int,
    `time` DATETIME NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `text` TEXT NOT NULL,
    `view_count` int NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `posts` ADD `user_id` int NOT NULL AFTER `moderator_id`;

CREATE TABLE `post_votes` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `time` DATETIME NOT NULL,
    `value` int NOT NULL


)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `post_votes` ADD `user_id` int NOT NULL AFTER `id`;
ALTER TABLE `post_votes` ADD `post_id` int NOT NULL AFTER `user_id`;

CREATE TABLE `tags` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `tag2post` (

     `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `tag2post` ADD `post_id` int NOT NULL AFTER `id`;
ALTER TABLE `tag2post` ADD `tag_id` int NOT NULL AFTER `post_id`;

CREATE TABLE `post_comments` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_id` int NOT NULL,
    `time` DATETIME NOT NULL,
    `text` TEXT NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE `post_comments` ADD `post_id` int NOT NULL AFTER `parent_id`;
ALTER TABLE `post_comments` ADD `user_id` int NOT NULL AFTER `post_id`;

CREATE TABLE `captcha_codes` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `time` DATETIME NOT NULL,
    `code` int NOT NULL,
    `secret_code` int NOT NULL

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `global_settings` (

    `code` VARCHAR(255),
    `name` VARCHAR(255),
    `value` VARCHAR(255)

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

INSERT INTO users (is_moderator,reg_time,name,email,password) VALUES (1, now(), 'Василий', 'vasily@mail.ru', '12345');

INSERT INTO users (is_moderator,reg_time,name,email,password) VALUES (0, now(), 'Петр', 'petr@mail.ru', '54321');

INSERT INTO posts (is_active,moderation_status,user_id,time,title,text,view_count) VALUES (1, "ACCEPTED", 1, now(), 'заголовок первого поста', 'текст первого поста', 0);

INSERT INTO posts (is_active,moderation_status,user_id,time,title,text,view_count) VALUES (0, "NEW", 2, now(), 'заголовок второго поста', 'текст второго поста', 0);