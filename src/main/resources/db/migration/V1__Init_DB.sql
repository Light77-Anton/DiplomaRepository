
CREATE TABLE users (

    id SERIAL NOT NULL,
    is_moderator SMALLINT NOT NULL,
    reg_time TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    code VARCHAR(255),
    photo TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE posts (

    id SERIAL NOT NULL,
    is_active SMALLINT NOT NULL,
    moderation_status VARCHAR(255) DEFAULT 'NEW',
    moderation_id INT,
    user_id INT NOT NULL,
    time TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    view_count INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE post_votes (

    id SERIAL NOT NULL,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    time TIMESTAMP NOT NULL,
    value SMALLINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE tags (

    id SERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE tag2post (

     id SERIAL NOT NULL,
     post_id INT NOT NULL,
     tag_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts (id),
     CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE TABLE post_comments (

    id SERIAL NOT NULL,
    parent_id INT NOT NULL,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    time TIMESTAMP NOT NULL,
    text TEXT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE captcha_codes (

    id SERIAL NOT NULL,
    time TIMESTAMP NOT NULL,
    code TEXT NOT NULL,
    secret_code TEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE global_settings (

    id SERIAL NOT NULL,
    code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL
);

INSERT INTO global_settings(code, name, value) VALUES ('MULTIUSER_MODE', 'Многопользовательский режим', 'YES');
INSERT INTO global_settings(code, name, value) VALUES ('POST_PREMODERATION', 'Премодерация постов', 'YES');
INSERT INTO global_settings(code, name, value) VALUES ('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога', 'YES');
INSERT INTO captcha_codes(code, secret_code, "time") VALUES ('t7sod', 'x3o10', now());
