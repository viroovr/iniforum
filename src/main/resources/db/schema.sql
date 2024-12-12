CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    content TEXT,
    tag VARCHAR(255),
    created_date TIMESTAMP NOT NULL,
    view_count INT DEFAULT 0
);