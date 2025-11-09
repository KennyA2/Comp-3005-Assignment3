import java.sql.*;                 
import java.time.LocalDate;        
import java.util.ArrayList;        
import java.util.List; 
import java.util.Scanner;

public class StudentSetup {
    private static final String URL = "jdbc:postgresql://localhost:5432/SchoolDatabase";
    private static final String USER = "postgres";
    private static final String PASSWORD = "yoru";
    // main model
    public static class Student {
        public Integer studentId;
        public String firstName;
        public String lastName;
        public String email;
        public LocalDate enrollmentDate;

        public Student(Integer id, String fn, String ln, String email, LocalDate ed) {
            this.studentId = id;
            this.firstName = fn;
            this.lastName = ln;
            this.email = email;
            this.enrollmentDate = ed;
        }

        @Override
        public String toString() {
            return String.format(
                "Student{id=%s, first='%s', last='%s', email='%s', enrolled=%s}",
                studentId, firstName, lastName, email, enrollmentDate
            );
        }
    }

    // class containing functions
    public static class StudentDatabase {
        private final Connection activeCon;

        public StudentDatabase(Connection activeCon) {
            this.activeCon = activeCon;
        }

        // get all student records.
        public List<Student> getAllStudents() throws SQLException {
            String line = "SELECT student_id, first_name, last_name, email, enrollment_date FROM students ORDER BY student_id";
            try (PreparedStatement s = activeCon.prepareStatement(line);
                 ResultSet results = s.executeQuery()) {
                List<Student> outList = new ArrayList<>();
                while (results.next()) {
                    outList.add(new Student(
                        results.getInt("student_id"),
                        results.getString("first_name"),
                        results.getString("last_name"),
                        results.getString("email"),
                        results.getDate("enrollment_date") == null ? null : results.getDate("enrollment_date").toLocalDate()
                    ));
                }
                return outList;
            }
        }

        // add Student
        public int addStudent(String firstName, String lastName, String email, LocalDate enrollmentDate) throws SQLException {
            String line = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement s = activeCon.prepareStatement(line)) {
                s.setString(1, firstName);
                s.setString(2, lastName);
                s.setString(3, email);
                if (enrollmentDate != null) {
                    s.setDate(4, Date.valueOf(enrollmentDate));
                } else {
                    s.setNull(4, Types.DATE);
                }
                return s.executeUpdate();
            }
        }

        // update student email
        public int updateStudentEmail(int studentId, String newEmail) throws SQLException {
            String line = "UPDATE students SET email = ? WHERE student_id = ?";
            try (PreparedStatement s = activeCon.prepareStatement(line)) {
                s.setString(1, newEmail);
                s.setInt(2, studentId);
                return s.executeUpdate(); 
            }
        }

        // delete student
        public int deleteStudent(int studentId) throws SQLException {
            String line = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement s = activeCon.prepareStatement(line)) {
                s.setInt(1, studentId);
                return s.executeUpdate(); 
            }
        }
    }

    private static LocalDate dateParser(String s) {
        if (s == null || s.isEmpty()) return null;
        return LocalDate.parse(s); 
    }

    private static Integer readInt(Scanner in) {
        String s = in.nextLine().trim();
        return Integer.parseInt(s);
    }
   

    //This is where I do the test
    public static void main(String[] args) {
        
        try (Connection activeCon = DriverManager.getConnection(URL, USER, PASSWORD)) {
            activeCon.setAutoCommit(true);
            StudentDatabase sd = new StudentDatabase(activeCon);
            Scanner in = new Scanner(System.in);
            while(true){
                System.out.println("\n Student Database Menu ");
                System.out.println("1) List all students");
                System.out.println("2) Add student");
                System.out.println("3) Update student email");
                System.out.println("4) Delete student");
                System.out.println("0) Exit");
                System.out.print("Choose: ");

                String option = in.nextLine().trim();
                try{
                    switch(option){
                        case "1": //List All students
                            System.out.println("\n All Students in Database");
                            sd.getAllStudents().forEach(System.out::println);
                            break;

                        case "2": // Add student
                            System.out.print("First name: ");
                            String fn = in.nextLine().trim();
                            System.out.print("Last name: ");
                            String ln = in.nextLine().trim();
                            System.out.print("Email: ");
                            String email = in.nextLine().trim();
                            System.out.print("Enrollment date (YYYY-MM-DD or blank): ");
                            String dateStr = in.nextLine().trim();
                            LocalDate ed = dateParser(dateStr);
                            int added = sd.addStudent(fn, ln, email, ed);
                            System.out.println(added == 1 ? "Student has been added." : "No row added.");
                            break;

                        case "3": // Update Student email
                            System.out.print("Student ID to update: ");
                            Integer uid = readInt(in);
                            System.out.print("New email: ");
                            String newEmail = in.nextLine().trim();
                            int upd = sd.updateStudentEmail(uid, newEmail);
                            System.out.println(upd == 1 ? "Email was updated." : "Unable to Update.");
                            break;

                        case "4": // delete student
                            System.out.print("Student ID to delete: ");
                            Integer did = readInt(in);
                            int del = sd.deleteStudent(did);
                            System.out.println(del == 1 ? " Student was deleted." : "Unable to delete.");
                            break;

                        case "0": // exit te program
                            System.out.println("Goodbye!");
                            return;

                        default:
                            System.out.println("Invalid option. Try 0-4.");                            

                    }

                }
                 catch (SQLException e) {
                System.err.println("error: " + e.getMessage());
               }

            }
            

            
            

        } 
        catch (SQLException e) {
            System.err.println("There was an error in SQL Connection: " + e.getMessage());
        }
    }
}