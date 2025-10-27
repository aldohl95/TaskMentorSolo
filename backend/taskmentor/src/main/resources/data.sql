-- Seed Users (passwords are 'password123' hashed with BCrypt)
INSERT INTO users (email, password, account_type) VALUES
 ('student1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
 ('student2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
 ('mentor1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MENTOR'),
 ('mentor2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MENTOR')
ON CONFLICT (email) DO NOTHING;

-- Seed Student Profiles
INSERT INTO students (user_id, first_name, last_name, bio, major, graduation_year, career_interests) VALUES
 (1, 'John', 'Doe', 'Computer Science student passionate about web development',
  'Computer Science', 2025, 'Software Engineering, Web Development'),
 (2, 'Jane', 'Smith', 'Business major looking to break into tech',
  'Business Administration', 2026, 'Product Management, Startups')
ON CONFLICT (user_id) DO NOTHING;

-- Seed Mentor Profiles
INSERT INTO mentors (user_id, first_name, last_name, bio, role_title, company, yrs_exp, industries, expertise) VALUES
    (3, 'Sarah', 'Johnson', 'Senior Software Engineer with 8 years experience',
     'Senior Software Engineer', 'Tech Corp', 8, 'Technology, SaaS',
     'Full-Stack Development, System Design'),
    (4, 'Michael', 'Chen', 'Product Manager helping students navigate tech careers',
     'Product Manager', 'StartupXYZ', 6, 'Technology, E-commerce',
     'Product Strategy, Career Guidance')
ON CONFLICT (user_id) DO NOTHING;

-- Seed Tasks
INSERT INTO tasks (mentor_id, title, description, duration, category) VALUES
 (1, 'Resume Review', 'I will review your resume and provide detailed feedback', 30,
  'CAREER_PREP'),
 (1, 'Mock Technical Interview', 'Practice coding interviews with real-world problems',
  60, 'INTERVIEW_PREP'),
 (2, 'Career Path Discussion', 'Discuss potential career paths in tech', 45,
  'CAREER_ADVICE'),
 (2, 'Product Management 101', 'Introduction to product management role', 30,
  'CAREER_ADVICE');
