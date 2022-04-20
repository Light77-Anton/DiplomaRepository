
CREATE TABLE users (

    id SERIAL NOT NULL,
    is_moderator INT NOT NULL,
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
    is_active INT NOT NULL,
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
    value INT NOT NULL,
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
    code VARCHAR(255) NOt NULL,
    name VARCHAR(255) NOt NULL,
    value VARCHAR(255) NOt NULL
);