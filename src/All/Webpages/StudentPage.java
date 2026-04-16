package All.Webpages;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import RunnablePack.Run;
import java.time.*;
import All.Exceptions.*;
public class StudentPage implements Page {
    Statement stmt = null;
    ResultSet rs = null;
    String Student_Id = null;
    Scanner sc = new Scanner(System.in);
    PreparedStatement ps = null;
    public StudentPage(String Id) {
        this.Student_Id = Id;
        if (Run.con == null) {
            Run.main();
            System.exit(-1);
        }
    }
    @Override
    public void showInfo() {
        System.out.println("********************* Student Page ******************************");
        System.out.println("Following are the keys: ");
        System.out.println("1. View Available courses");
        System.out.println("2. Register for courses");
        System.out.println("3. View Schedule");
        System.out.println("4. Track Academic Progress");
        System.out.println("5. Drop Courses");
        System.out.println("6. Submit Complaints");
        System.out.println("7. Check Complain status");
        System.out.println("8. Give Feedback on a course");
        System.out.println("9. Exit Website");
    }
    public void main() {


        try {
            PreparedStatement check = Run.con.prepareStatement(
                    "SELECT student_id FROM gpa WHERE student_id = ?"
            );
            check.setString(1, this.Student_Id);

            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                PreparedStatement insert = Run.con.prepareStatement(
                        "INSERT INTO gpa (student_id) VALUES (?)"
                );
                insert.setString(1, this.Student_Id);

                int x = insert.executeUpdate();
                if (x < 0) {
                    System.out.println("Insertion failed");
                }
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }



        int choice = 0;
        while (true) {
            this.showInfo();
            System.out.print("Enter :");
            try {
                choice = Integer.parseInt(sc.nextLine());
                this.stmt = Run.con.createStatement();

            } catch (NumberFormatException e) {
                System.out.println("Enter valid expression");
                continue;
            } catch (Exception e) {
                System.out.println(e);
                continue;
            }

            switch (choice) {
                case 1:
                    getCourses();
                    break;
                case 2:
                    enroll();
                    break;
                case 3:
                    viewSchedule();
                    break;
                case 4:
                    track();
                    break;
                case 5:
                    drop();
                    break;
                case 6:
                    complain();
                    break;
                case 7:
                    complainStatus();
                    break;
                case 8:
                    feedback();
                    break;
                case 9:
                    System.out.println("Exit..");
                    System.exit(-1);
                default:
                    System.out.println("Enter a valid Choice");
                    continue;
            }
        }

    }
    void getCourses() {
        /*Students should be able to see all available courses for the current semester, including course code, title, professor, credits, prerequisites, and timings.*/
        try {

            System.out.printf("%-15s  %-30s  %-25s  %-25s  %-25s  %-25s\n", "Course code", "Course title", "Professor", "Credits", "Prereq", "Timings");
            //this.rs = stmt.executeQuery("select semester from students where Student_id= ?");
            PreparedStatement ps = Run.con.prepareStatement("select semester from students where Student_id= ?");
            ps.setString(1, this.Student_Id);
            this.rs = ps.executeQuery();
            rs.next();
            int sem = Integer.parseInt(rs.getString("semester"));
            this.rs = stmt.executeQuery("select * from courses where semester=" + sem);
            while (rs.next()) {
                String code = rs.getString("course_id");
                String title = rs.getString("course_name");
                String professor = rs.getString("professor");
                int credits = rs.getInt("credits");
                String prereq = rs.getString("prerequisites");
                String timings = rs.getString("schedule");
                System.out.printf("%-15s  %-30s  %-25s  %-25s  %-25s  %-25s\n", code, title, professor, credits, prereq, timings);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    void enroll() {
        int n;
        while (true) {
            try {
                System.out.print("Number of courses: ");
                n = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Please enter a valid number..");
                continue;
            }
        }


        while (n != 0) {
            try {
                System.out.print("Enter course code: ");
                PreparedStatement ps = Run.con.prepareStatement(
                        "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)"
                );
                String course_id = sc.nextLine();
                if(course_id.equals("\n"))break;
                ps.setString(1, this.Student_Id);
                ps.setString(2, course_id);
                PreparedStatement pss = null;
                pss = Run.con.prepareStatement("Select course_id from enrollments where student_id = ?");
                pss.setString(1, this.Student_Id);
                rs = pss.executeQuery();
                ArrayList<String> enrolled = new ArrayList<String>();
                enrolled.add("null");
                while (rs.next()) {
                    enrolled.add(rs.getString("course_id"));
                }

                pss = Run.con.prepareStatement("Select prerequisites,BatchSize,Current_Size,Deadline from courses where course_id = ?");
                pss.setString(1, course_id);
                rs = pss.executeQuery();
                ArrayList<String> prereq = new ArrayList<String>();
                int i = 0;
                rs.next();
                // System.out.println("\nPrerequesi:  ");
                if (rs.getString("prerequisites") != null) {
                    String[] list = rs.getString("prerequisites").split(",");
                    for (String s : list) {
                        prereq.add(s);
                    }
                } else {
                    prereq.add("null");
                }
                if (enrolled.containsAll(prereq)) {
                        if(rs.getInt("BatchSize")>rs.getInt("Current_Size")){


                            ps.executeUpdate();
                            PreparedStatement inc = Run.con.prepareStatement(
                                    "UPDATE courses SET Current_Size = Current_Size + 1 WHERE course_id = ?"
                            );
                            inc.setString(1, course_id);
                            System.out.println("Course added successfully!");
                        }
                        else{
                            throw new CourseFullException();
                        }
                    n--;
                } else {
                    System.out.println("The prerequisites for this course have not been satisfied ");
                }

            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("This course has already been registered");
            } catch (CourseFullException e) {
                System.out.println(e);
                return;
            }
            catch (Exception e) {
                System.out.println("Invalid Course");
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
                continue;
            }
        }
    }
    void viewSchedule() {
        try {
            String query = "SELECT c.course_id, c.schedule,c.course_name FROM enrollments e JOIN courses c ON e.course_id = c.course_id WHERE e.student_id = ?";
            PreparedStatement ps = Run.con.prepareStatement(query);
            ps.setString(1, this.Student_Id);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-12s   %-25s   %-20s%n",
                    "Course ID", "Title", "Schedule");

            System.out.println("--------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-12s %-30s %-20s%n",
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("schedule")

                );
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    void track() {
        try {
            PreparedStatement ps = Run.con.prepareStatement("Select * from gpa where student_id  = ?");
            ps.setString(1, this.Student_Id);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.printf("%-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n",
                        "Sem1", "Sem2", "Sem3", "Sem4", "Sem5", "Sem6", "Sem7", "Sem8", "CGPA");

                System.out.println("--------------------------------------------------------------------------");

                System.out.printf("%-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n",
                        rs.getObject("sem1"),
                        rs.getObject("sem2"),
                        rs.getObject("sem3"),
                        rs.getObject("sem4"),
                        rs.getObject("sem5"),
                        rs.getObject("sem6"),
                        rs.getObject("sem7"),
                        rs.getObject("sem8"),
                        rs.getObject("cgpa")
                );
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    void drop() {
        while (true) {
            try {
                System.out.println("Enter Course Id: ");
                String course_id = sc.nextLine();
                PreparedStatement ps = Run.con.prepareStatement("Select * from courses where course_id = ?");
                ps.setString(1,course_id);
                rs = ps.executeQuery();
                LocalDate deadline = rs.getDate("Deadline").toLocalDate();
                            LocalDate today = LocalDate.now();
                            if(today.isAfter(deadline)){
                                throw new DropDeadlinePassedException();
                            }
                ps = Run.con.prepareStatement("Delete from enrollments where student_id = ? and course_id = ?");
                ps.setString(1, this.Student_Id);
                ps.setString(2, course_id);

                if (ps.executeUpdate() > 0) {
                    System.out.println("Course deleted successfully!!");
                    break;
                } else {
                    System.out.println("NO SUCH COURSE EXISTS. \n PLEASE RETRY..");
                }
            }catch(DropDeadlinePassedException e){
                System.out.println(e);
                continue;
            }
            catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
                System.out.println("Please enter a valid Course..");
                continue;
            }
        }

    }
    void complain() {
        System.out.println("Enter complain:");
        String complain = sc.nextLine();
        try {
            PreparedStatement ps = Run.con.prepareStatement("INSERT INTO complaints (student_id, complaint) VALUES (?, ?)");
            ps.setString(1, this.Student_Id);
            ps.setString(2, complain);
            ps.executeUpdate();
            ps = Run.con.prepareStatement(
                    "SELECT complaint_id FROM complaints WHERE student_id = ? ORDER BY complaint_id DESC LIMIT 1"
            );
            ps.setString(1, this.Student_Id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Complain Added successfully");
                int lastId = rs.getInt("complaint_id");
                System.out.println("Complaint ID: " + lastId);
            }

        } catch (SQLException e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }


    }
    void complainStatus() {
        try {
            PreparedStatement ps = Run.con.prepareStatement(
                    "SELECT complaint_id, complaint, status FROM complaints WHERE student_id = ?"
            );
            ps.setString(1, this.Student_Id);

            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("No complaints found");
                return;
            }

            System.out.println("Your Complaints:\n");

            do {
                int id = rs.getInt("complaint_id");
                String text = rs.getString("complaint");
                String status;

                // handle both BOOLEAN and ENUM cases
                try {
                    status = rs.getString("status"); // works for ENUM
                    if (status == null) {
                        status = rs.getBoolean("status") ? "Resolved" : "Pending";
                    }
                } catch (Exception e) {
                    status = rs.getBoolean("status") ? "Resolved" : "Pending";
                }

                System.out.println("Complaint ID: " + id);
                System.out.println("Complaint: " + text);
                System.out.println("Status: " + status);
                System.out.println("-----------------------------");

            } while (rs.next());

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    void feedback(){

        try {
            System.out.println("Enter course code: ");
            String course_id = sc.nextLine();
            ps = Run.con.prepareStatement("Select student_id from enrollments where course_id = ?");
            ps.setString(1,course_id);
            rs = ps.executeQuery();
            FeedBack<?>F = null;
            if(rs.next()){
                System.out.println("Give  feedback: ");
                String input="";
                try {
                    input = sc.nextLine();
                    F = new FeedBack<>(Float.valueOf(input));
                } catch (NumberFormatException e) {
                    F = new FeedBack<>(input);
                }
            }
            else{
                System.out.println("You have not enrolled in this course");
            }
            String fb = F.getFeedBack();
            System.out.println("This is the feedback: "+fb);
            ps = Run.con.prepareStatement("Insert into feedback values (?,?,?)");
            ps.setString(1,this.Student_Id);
            ps.setString(2,course_id);
            ps.setString(3,fb);

            if(ps.executeUpdate()!=0){
                System.out.println("FeedBack added successfully");
            }
            else{
                System.out.println("feedback Could not be updated ");
            }
        }catch(SQLIntegrityConstraintViolationException e){
            System.out.println("You have already given a feedback for this course");
        }
        catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }


    }
}