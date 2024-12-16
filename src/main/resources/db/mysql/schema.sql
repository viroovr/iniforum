CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    content CLOB NOT NULL,
    tag VARCHAR(255),
    created_date TIMESTAMP NOT NULL,
    view_count INT DEFAULT 0
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content CLOB NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    like_count BIGINT DEFAULT 0,
    question_id BIGINT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);
