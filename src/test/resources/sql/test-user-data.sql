INSERT INTO users (
    id, login_id, email, password, last_name, first_name, nickname, profile_image_path,
    status, role, last_activity_date, password_last_modified_date, last_login_date, created_date
)
VALUES (
    100,
    'testUser',
    'testUser@example.com',
    'password123',
    'Doe',
    'John',
    'tester',
    '/images/profile1.png',
    'ACTIVE',
    'USER',
    '2024-12-18T10:00:00',
    '2024-12-01T12:00:00',
    '2024-12-18T12:00:00',
    '2024-12-01T08:00:00'
);