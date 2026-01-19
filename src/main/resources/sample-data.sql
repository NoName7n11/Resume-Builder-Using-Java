-- Sample Data for Java Resume Builder
-- Run this in H2 console to populate with test data

-- Insert a test user
INSERT INTO users (id, email, password, first_name, last_name, provider, enabled, created_at, updated_at)
VALUES (1, 'john.doe@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'John', 'Doe', 'LOCAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert user role
INSERT INTO user_roles (user_id, role) VALUES (1, 'USER');

-- Create a resume
INSERT INTO resumes (id, user_id, title, description, template_name, professional_summary, active, share_enabled, created_at, updated_at)
VALUES (1, 1, 'Software Engineer Resume', 'My professional software engineering resume', 'professional', 
'Experienced Full-Stack Software Engineer with 5+ years of expertise in Java, Spring Boot, and modern web technologies. Proven track record of delivering high-quality applications and leading development teams. Passionate about clean code, best practices, and continuous learning.', 
true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert personal information
INSERT INTO personal_info (id, resume_id, first_name, last_name, email, phone, address, city, state, zip_code, country, linkedin_url, github_url, portfolio_url)
VALUES (1, 1, 'John', 'Doe', 'john.doe@example.com', '(555) 123-4567', '123 Main Street', 'San Francisco', 'CA', '94102', 'USA', 
'https://linkedin.com/in/johndoe', 'https://github.com/johndoe', 'https://johndoe.dev');

-- Insert education entries
INSERT INTO education (id, resume_id, degree, field_of_study, institution, location, start_date, end_date, is_current, gpa, gpa_scale, description, achievements, display_order)
VALUES 
(1, 1, 'Bachelor of Science', 'Computer Science', 'Stanford University', 'Stanford, CA', '2014-09-01', '2018-05-15', false, 3.8, 4.0, 
'Focused on software engineering and algorithms', 'Dean''s List (2016-2018), President of Computer Science Club', 0),
(2, 1, 'Master of Science', 'Software Engineering', 'MIT', 'Cambridge, MA', '2018-09-01', '2020-05-20', false, 3.9, 4.0,
'Specialized in distributed systems and cloud computing', 'Research Assistant, Published 2 papers', 1);

-- Insert work experiences
INSERT INTO work_experience (id, resume_id, job_title, company, location, start_date, end_date, is_current, description, responsibilities, achievements, display_order)
VALUES 
(1, 1, 'Senior Software Engineer', 'Tech Giants Inc.', 'San Francisco, CA', '2022-01-01', NULL, true, 
'Leading development of microservices architecture for e-commerce platform',
'Led team of 5 engineers in designing and implementing RESTful APIs
Architected cloud-native solutions using AWS and Kubernetes
Implemented CI/CD pipelines reducing deployment time by 60%
Mentored junior developers and conducted code reviews',
'Improved system performance by 40% through optimization
Reduced bug count by 55% through comprehensive testing
Increased team productivity by 30% through process improvements', 0),
(2, 1, 'Software Engineer', 'StartupXYZ', 'Palo Alto, CA', '2020-06-01', '2021-12-31', false,
'Full-stack development for SaaS platform',
'Developed features using Java Spring Boot and React
Designed and implemented database schemas with PostgreSQL
Integrated third-party APIs and payment systems
Participated in agile ceremonies and sprint planning',
'Built MVP that acquired first 100 customers
Reduced API response time by 50%
Implemented features that increased user engagement by 35%', 1),
(3, 1, 'Junior Software Developer', 'Enterprise Solutions LLC', 'San Jose, CA', '2018-06-01', '2020-05-31', false,
'Backend development and maintenance',
'Developed backend services using Java and Spring
Fixed bugs and implemented new features
Wrote unit tests and integration tests
Collaborated with QA team on testing strategies',
'Resolved 150+ bugs in first year
Achieved 90% code coverage through testing
Received Employee of the Month award twice', 2);

-- Insert skills
INSERT INTO skills (id, resume_id, name, category, proficiency_level, display_order, visible)
VALUES 
(1, 1, 'Java', 'Programming Languages', 'EXPERT', 1, true),
(2, 1, 'Python', 'Programming Languages', 'ADVANCED', 2, true),
(3, 1, 'JavaScript', 'Programming Languages', 'ADVANCED', 3, true),
(4, 1, 'TypeScript', 'Programming Languages', 'INTERMEDIATE', 4, true),
(5, 1, 'Spring Boot', 'Frameworks', 'EXPERT', 5, true),
(6, 1, 'React', 'Frameworks', 'ADVANCED', 6, true),
(7, 1, 'Node.js', 'Frameworks', 'ADVANCED', 7, true),
(8, 1, 'Angular', 'Frameworks', 'INTERMEDIATE', 8, true),
(9, 1, 'PostgreSQL', 'Databases', 'ADVANCED', 9, true),
(10, 1, 'MongoDB', 'Databases', 'ADVANCED', 10, true),
(11, 1, 'Redis', 'Databases', 'INTERMEDIATE', 11, true),
(12, 1, 'AWS', 'Cloud & DevOps', 'ADVANCED', 12, true),
(13, 1, 'Docker', 'Cloud & DevOps', 'ADVANCED', 13, true),
(14, 1, 'Kubernetes', 'Cloud & DevOps', 'INTERMEDIATE', 14, true),
(15, 1, 'Jenkins', 'Cloud & DevOps', 'ADVANCED', 15, true),
(16, 1, 'Git', 'Tools', 'EXPERT', 16, true),
(17, 1, 'JIRA', 'Tools', 'ADVANCED', 17, true),
(18, 1, 'Leadership', 'Soft Skills', 'ADVANCED', 18, true),
(19, 1, 'Communication', 'Soft Skills', 'EXPERT', 19, true),
(20, 1, 'Problem Solving', 'Soft Skills', 'EXPERT', 20, true);

-- Insert projects
INSERT INTO projects (id, resume_id, name, description, technologies, project_url, github_url, start_date, end_date, is_current, highlights, role, display_order)
VALUES 
(1, 1, 'E-Commerce Microservices Platform', 
'Built scalable microservices architecture for high-traffic e-commerce platform handling 1M+ daily users',
'Java, Spring Boot, Kubernetes, PostgreSQL, Redis, Kafka',
'https://platform.example.com',
'https://github.com/johndoe/ecommerce-platform',
'2022-03-01', NULL, true,
'Achieved 99.9% uptime with auto-scaling
Reduced infrastructure costs by 40%
Processed 10M+ transactions/month
Implemented event-driven architecture with Kafka',
'Lead Developer', 0),

(2, 1, 'Real-Time Analytics Dashboard',
'Developed real-time analytics dashboard for business intelligence using streaming data',
'React, Node.js, MongoDB, WebSockets, D3.js',
'https://analytics.example.com',
'https://github.com/johndoe/analytics-dashboard',
'2021-06-01', '2022-02-28', false,
'Visualized 100K+ data points in real-time
Reduced data refresh latency to <100ms
Increased data accuracy by 95%
Used by 500+ business analysts daily',
'Full-Stack Developer', 1),

(3, 1, 'Open Source Library - JavaUtils',
'Created and maintain popular open-source Java utility library with 2K+ GitHub stars',
'Java, Maven, JUnit, GitHub Actions',
NULL,
'https://github.com/johndoe/java-utils',
'2020-01-01', NULL, true,
'2,000+ GitHub stars and 500+ forks
Downloaded 50K+ times via Maven Central
Active community with 20+ contributors
Comprehensive documentation and examples',
'Creator & Maintainer', 2);

-- Insert custom sections
INSERT INTO custom_sections (id, resume_id, section_title, content, content_type, display_order, visible)
VALUES 
(1, 1, 'CERTIFICATIONS', 
'AWS Certified Solutions Architect - Professional (2023)
Oracle Certified Professional, Java SE 11 Developer (2022)
Certified Kubernetes Administrator (CKA) (2021)
Scrum Master Certified (SMC) (2020)',
'BULLET_LIST', 0, true),

(2, 1, 'PUBLICATIONS',
'Doe, J. & Smith, A. (2020). "Optimizing Microservices Architecture for Scale." IEEE Software Engineering Journal.
Doe, J. (2019). "Modern Java Development Best Practices." Conference on Software Engineering.',
'BULLET_LIST', 1, true),

(3, 1, 'VOLUNTEER EXPERIENCE',
'Code Mentor, Code.org - Teaching programming to underserved youth (2020-Present)
Tech Speaker, Local Java User Group - Monthly technical presentations (2019-Present)',
'BULLET_LIST', 2, true),

(4, 1, 'LANGUAGES',
'English (Native)
Spanish (Professional Working Proficiency)
Mandarin Chinese (Elementary)',
'BULLET_LIST', 3, true);

-- Insert resume settings
UPDATE resumes SET 
    primary_color = '#2c3e50',
    secondary_color = '#3498db',
    font_family = 'Arial',
    font_size = 11,
    line_spacing = 1.15,
    margin_top = 20,
    margin_bottom = 20,
    margin_left = 20,
    margin_right = 20,
    show_profile_photo = false,
    section_order = 'personal,summary,experience,education,skills,projects,custom'
WHERE id = 1;

-- Verify data
SELECT 'Users:' as entity, COUNT(*) as count FROM users
UNION ALL
SELECT 'Resumes:', COUNT(*) FROM resumes
UNION ALL
SELECT 'Personal Info:', COUNT(*) FROM personal_info
UNION ALL
SELECT 'Education:', COUNT(*) FROM education
UNION ALL
SELECT 'Work Experience:', COUNT(*) FROM work_experience
UNION ALL
SELECT 'Skills:', COUNT(*) FROM skills
UNION ALL
SELECT 'Projects:', COUNT(*) FROM projects
UNION ALL
SELECT 'Custom Sections:', COUNT(*) FROM custom_sections;
