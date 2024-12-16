CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP,
    profile_image_path VARCHAR(255),
    nickname VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    content TEXT,
    tag VARCHAR(255),
    created_date TIMESTAMP NOT NULL,
    view_count INT DEFAULT 0
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    question_id BIGINT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_comment ON likes (user_id, comment_id);
