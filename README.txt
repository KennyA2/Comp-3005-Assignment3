Student Name: Kenny Adewoye
Student Number: 101313388

Database Setup:
1. In pgAdmin, name Database 'SchoolDatabase'
2. Insert the starter data 
CREATE TABLE IF NOT EXISTS students (
  student_id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  first_name      TEXT NOT NULL,
  last_name       TEXT NOT NULL,
  email           TEXT NOT NULL UNIQUE,
  enrollment_date DATE
);

INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES
('John', 'Doe', 'john.doe@example.com', '2023-09-01'),
('Jane', 'Smith', 'jane.smith@example.com', '2023-09-01'),
('Jim',  'Beam',  'jim.beam@example.com',  '2023-09-02')
ON CONFLICT (email) DO NOTHING;

3. After this is done, you can open another Query tab and put in the following Query:	SELECT * FROM students;




Execution:
To Compile application, go into folder containing the files
-Type "javac -cp postgresql-42.7.8.jar StudentSetup.java" to compile
-To run the application, type "java -cp postgresql-42.7.8.jar;. StudentSetup"
-The application has a menu which will make testing easier


Video Link:
https://www.youtube.com/watch?v=t766g9REHlo
