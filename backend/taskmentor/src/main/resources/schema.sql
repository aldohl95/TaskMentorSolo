--Users table
CREATE TABLE IF NOT EXISTS users(
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    account_type VARCHAR(255) NOT NULL CHECK (account_type IN('STUDENT', 'MENTOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--Students Table
CREATE TABLE IF NOT EXISTS students(
    student_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    bio TEXT,
    major VARCHAR(100),
    graduation_year INTEGER,
    career_interests TEXT
);

--Mentors table
CREATE TABLE IF NOT EXISTS  mentors(
    mentor_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    bio TEXT,
    role_title VARCHAR(100),
    company VARCHAR(100),
    yrs_exp VARCHAR(50),
    industries TEXT,
    expertise TEXT,
    photo_url VARCHAR(500)
);

--Tasks table
CREATE TABLE IF NOT EXISTS tasks(
    task_id BIGSERIAL PRIMARY KEY,
    mentor_id BIGINT NOT NULL REFERENCES mentors(mentor_id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    duration INTEGER,
    category VARCHAR(100)
);

--Bookings Table
Create TABLE IF NOT EXISTS bookings(
    booking_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    mentor_id BIGINT NOT NULL REFERENCES mentors(mentor_id) ON DELETE CASCADE,
    task_id BIGINT NOT NULL REFERENCES tasks(task_id) ON DELETE CASCADE,
    proposed_date TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        check (status in ('PENDING', 'ACCEPTED', 'DECLINED', 'COMPLETED','CANCELLED')),
    student_msg TEXT,
    mentor_resp TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

--Indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_students_user_id ON students(user_id);
CREATE INDEX IF NOT EXISTS idx_mentors_user_id ON mentors(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_mentor_id ON tasks(mentor_id);
CREATE INDEX IF NOT EXISTS idx_bookings_student_id ON bookings(student_id);
CREATE INDEX IF NOT EXISTS idx_bookings_mentor_id ON bookings(mentor_id);
CREATE INDEX IF NOT EXISTS idx_bookings_task_id ON bookings(task_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);