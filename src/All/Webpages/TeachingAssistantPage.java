package All.Webpages;

import RunnablePack.Run;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TeachingAssistantPage extends StudentPage implements Page {
    public TeachingAssistantPage(String Id) {
        super(Id);
    }

    @Override
    public void showInfo() {
        System.out.println("********************* TA Page ******************************");
        System.out.println("Following are the keys: ");
        System.out.println("1. View Grades of students");
        System.out.println("2. Update grades of students");
        System.out.println("3. View Courses.");


    }

    @Override
    public void main() {
        this.showInfo();
        while(true) {
            try {
                System.out.print("Enter Choice: ");
                switch (Integer.parseInt(sc.nextLine())) {
                    case 1:
                        view_grades();
                        break;
                    case 2:
                        update_grades();
                        break;
                    case 3:
                        getCourses();
                        break;
                    default:
                        System.out.println("Invalid Number given");
                        continue;

                }
            } catch (Exception e) {
                System.out.println("An unknown error occurred : at 42 of TA.java : "+e);
                continue;
            }
        }
    }
    void view_grades(){
        try {
            System.out.println("Enter semester: ");
            int sem = Integer.parseInt(sc.nextLine());
            PreparedStatement ps = Run.con.prepareStatement("Select * from grades where semester = ?");
            ps.setInt(1,sem);
            rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                for (int i = 1; i <= 4; i++) {
                    String val = rs.getString(i);
                    if (val == null) val = "NULL";
                    System.out.printf("%-20s"+" | ", val);
                }
                System.out.println();
            }

            if (!found) {
                System.out.println("No records found for this semester");
            }
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }

    }
    void update_grades(){
        System.out.println("Enter Student id: ");
        String student_id = sc.nextLine();
        System.out.println("Enter Course id: ");
        String course_id = sc.nextLine();
        try {
            PreparedStatement ps = Run.con.prepareStatement("Select * from grades where student_id = ? and course_id = ?");
            ps.setString(1,student_id);
            ps.setString(2,course_id);
            rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                for (int i = 1; i <= 4; i++) {
                    String val = rs.getString(i);
                    if (val == null) val = "NULL";
                    System.out.printf("%-20s"+" | ", val);
                }
                System.out.println();
            }

            if (!found) {
                System.out.println("No Data Found");
            }
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
        while(true){
            try {
                System.out.println("Enter grade : ");
                String grade = sc.nextLine().toUpperCase();

                // Basic validation
                if (!(grade.equals("A") || grade.equals("B") || grade.equals("C") || grade.equals("F"))) {
                    System.out.println("Invalid grade");
                    continue;
                }
                PreparedStatement ps = Run.con.prepareStatement(
                        "UPDATE grades SET grade = ? WHERE student_id = ? AND course_id = ?"
                );

                ps.setString(1, grade);
                ps.setString(2, student_id);
                ps.setString(3, course_id);
                if (ps.executeUpdate() > 0) {
                    System.out.println("Grade updated successfully");
                    return;
                } else {
                    System.out.println("Grade Updation failed");
                    continue;
                }
            } catch (SQLException e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }

        }

    }
    @Override
    void getCourses(){
        try {

            System.out.printf("%-15s  %-30s  %-25s  %-25s  %-25s  %-25s\n", "Course code", "Course title", "Professor", "Credits", "Prereq", "Timings");

            stmt = Run.con.createStatement();
            this.rs = stmt.executeQuery("select * from courses");
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
}
