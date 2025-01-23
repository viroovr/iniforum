-- Users 데이터 삽입
INSERT INTO users (login_id, email, password, last_name, first_name, nickname, profile_image_path, status, role)
VALUES 
('john_doe', 'john.doe@example.com', 'hashed_password_1', 'Doe', 'John', 'Johnny', '/images/profile/john_doe.png', 'ACTIVE', 'USER'),
('jane_doe', 'jane.doe@example.com', 'hashed_password_2', 'Doe', 'Jane', 'Janie', '/images/profile/jane_doe.png', 'ACTIVE', 'USER'),
('admin_user', 'admin@example.com', 'hashed_password_3', 'Admin', 'Super', 'Admin', '/images/profile/admin_user.png', 'ACTIVE', 'ADMIN'),
('alice_smith', 'alice.smith@example.com', 'hashed_password_4', 'Smith', 'Alice', 'AliceS', '/images/profile/alice_smith.png', 'INACTIVE', 'USER'),
('bob_brown', 'bob.brown@example.com', 'hashed_password_5', 'Brown', 'Bob', 'BobB', '/images/profile/bob_brown.png', 'ACTIVE', 'USER');

-- Questions 데이터 삽입
INSERT INTO questions (user_id, login_id, title, content, status)
VALUES 
(1, 'john_doe', 'What is Java?', 'Can someone explain what Java is and its main use cases?', 'OPEN'),
(1, 'john_doe', 'How to learn Spring Boot?', 'I am new to Spring Boot. What resources do you recommend?', 'OPEN'),
(2, 'jane_doe', 'Best practices for REST APIs?', 'What are the best practices for designing RESTful APIs?', 'OPEN'),
(3, 'admin_user', 'Database optimization tips', 'How do I optimize database queries for better performance?', 'CLOSED'),
(3, 'admin_user', 'Security in web development', 'What are common security measures for web applications?', 'OPEN'),
(4, 'alice_smith', 'CSS vs SCSS', 'What is the difference between CSS and SCSS, and when to use each?', 'OPEN'),
(4, 'alice_smith', 'Frontend vs Backend Development', 'What are the main differences between frontend and backend development?', 'OPEN'),
(5, 'bob_brown', 'Introduction to Machine Learning', 'Where should a beginner start learning machine learning?', 'OPEN'),
(5, 'bob_brown', 'What is Kubernetes?', 'Can someone explain Kubernetes in simple terms?', 'OPEN'),
(5, 'bob_brown', 'Using Docker effectively', 'What are some tips for using Docker effectively in a development workflow?', 'OPEN');

-- 추가적인 질문 데이터 삽입
INSERT INTO questions (user_id, login_id, title, content, status)
VALUES 
(2, 'jane_doe', 'How to secure REST APIs?', 'What are the best ways to secure REST APIs?', 'OPEN'),
(1, 'john_doe', 'What is OAuth2?', 'Can someone explain OAuth2 and how it is used in modern applications?', 'OPEN'),
(4, 'alice_smith', 'What is Responsive Design?', 'What does responsive design mean in web development?', 'OPEN'),
(3, 'admin_user', 'What is SQL Injection?', 'How do you prevent SQL injection attacks in web applications?', 'CLOSED'),
(2, 'jane_doe', 'Understanding JWT', 'How does JWT work and why is it used?', 'OPEN');
