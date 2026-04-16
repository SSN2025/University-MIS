package All.Webpages;
import java.sql.PreparedStatement;
import java.sql.*;
import java.util.Scanner;
import RunnablePack.Run;

public class AdministratorPage implements Page{
    Statement stmt =null;
    ResultSet rs = null;
    Connection con   = null;
    Scanner sc = new Scanner(System.in);
    public AdministratorPage(String id){
        if(Run.con == null){Run.main();
            System.exit(-1);
    }
    }
    public  void main(){
        int choice = 0;
        this.con = Run.con;
        while(true) {
            try {
            this.showInfo();
            System.out.print("Enter :");
            choice = Integer.parseInt(sc.nextLine());
                this.stmt = Run.con.createStatement();

            }catch(NumberFormatException e){
                System.out.println("Invalid Input");
                continue;
            }
            catch (Exception e) {
                System.out.println(e);
            }
            switch (choice) {
                case 1:
                    manageCourse();
                    break;
                case 2:
                    manageStudent();
                    break;
                case 3:
                    assignProf();
                    break;

                case 4:
                    handleComplains();
                    break;
                case 5:
                    System.exit(-1);
                default:
                    System.out.println("enter Valid Input ");
                    continue;
            }
        }

    }


    public void showInfo(){
        System.out.println("*********************Administrator Page******************************");
        System.out.println("1. Manage Course Catalog");
        System.out.println("2. Manage Student Records");
        System.out.println("3. Assign Professors to Courses");
        System.out.println("4. Handle Complaints");
        System.out.println("5. Exit");
    }
    public void manageCourse(){
        /*Administrators should be able to view, add, and delete courses from the course catalog.*/
        while(true){
        System.out.println("1. Add course");
        System.out.println("2. View course");
        System.out.println("3. delete course");
        System.out.println("4. exit manage courses");
        System.out.print("Enter :");
        int choice2 = Integer.parseInt(sc.nextLine());
        switch(choice2) {
            case 1:
                addCourse();
                break;
            case 2:
                viewCourse();
                break;
            case 3:
                dropCourse();
                break;
            case 4:
               return;
        }
        }
    }
    public void viewCourse(){
        try{

            System.out.printf("%-15s  %-30s  %-25s  %-25s  %-25s  %-25s\n","Course code","Course title","Professor","Credits","Prereq","Timings");

            this.rs = stmt.executeQuery("select * from courses;");
            while(rs.next()) {
                String code = rs.getString("course_id");
                String title = rs.getString("course_name");
                String professor = rs.getString("professor");
                int credits = rs.getInt("credits");
                String prereq = rs.getString("prerequisites");
                String timings = rs.getString("schedule");
                System.out.printf("%-15s  %-30s  %-25s  %-25s  %-25s  %-25s\n", code, title, professor, credits, prereq, timings);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void addCourse(){
        this.viewCourse();
        sc.nextLine();
        System.out.print("Enter Course Code: ");
        String course_id = sc.nextLine();

        System.out.print("Enter Course Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Professor Name: ");
        String professor = sc.nextLine();

        System.out.print("Enter Credits: ");
        int credits = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Prerequisites: ");
        String prerequisites = sc.nextLine();

        System.out.print("Enter Timings: ");
        String timings = sc.nextLine();

        String query = "INSERT INTO courses VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = Run.con.prepareStatement(query);

            pstmt.setString(1, course_id);
            pstmt.setString(2, title);
            pstmt.setString(3, professor);
            pstmt.setInt(4, credits);
            pstmt.setString(5, prerequisites);
            pstmt.setString(6, timings);

            pstmt.executeUpdate();
            viewCourse();
        }
        catch(Exception e){
            System.out.println(e);
        }

    }
    public void dropCourse(){

            try {

                System.out.print("Enter Course Code to delete: ");
                String code = sc.nextLine();

                String checkQuery = "SELECT * FROM Courses WHERE course_id = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setString(1, code);

                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Course not found.");
                    return;
                }

                // Step 3: Show course details (optional but better UX)
                System.out.println("Course Found: " + rs.getString(" course_name ") +
                        " by " + rs.getString("professor"));

                // Step 4: Confirmation
                System.out.print("Are you sure you want to delete this course? (yes/no): ");
                String confirm = sc.nextLine();

                if (!confirm.equalsIgnoreCase("yes")) {
                    System.out.println("Deletion cancelled.");
                    return;
                }

                // Step 5: Delete
                String deleteQuery = "DELETE FROM Courses WHERE course_id = ?";
                PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
                deleteStmt.setString(1, code);

                int rows = deleteStmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("Course deleted successfully.");
                } else {
                    System.out.println("Deletion failed.");
                }

            } catch (Exception e) {
                System.out.println("An unknown error occurred : at 196 of AdminPage.java : "+e);
            }
            finally{
                viewCourse();
            }

    }
    public void handleComplains(){
        try {
            PreparedStatement ps = Run.con.prepareStatement("SELECT * FROM complaints");
            ResultSet rs = ps.executeQuery();

            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            int width = 25; // column width (adjust if needed)

            // Print headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-" + 50 + "s", rsmd.getColumnName(i));
            }
            System.out.println();


            // Print separator line
            for (int i = 1; i <= columnCount * width; i++) {
                System.out.print("-");
            }
            System.out.println();

            boolean hasData = false;

            // Print rows
            while (rs.next()) {
                hasData = true;
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value == null) value = "NULL";
                    System.out.printf("%-" + 50 + "s", value);
                }
                System.out.println();
            }

            if (!hasData) {
                System.out.println("No complaints found");
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }


    }
    public void assignProf(){
        System.out.print("Enter Course Code: ");
        String course_code = sc.nextLine();

        try {
            // Step 1: Check course and get current professor
            PreparedStatement ps = Run.con.prepareStatement(
                    "SELECT professor, professor_id FROM courses WHERE course_id = ?"
            );
            ps.setString(1, course_code);
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid Course Code");
                return;
            }

            System.out.println("Current Professor:");
            System.out.println("Name       Id");
            System.out.println(rs.getString("professor") + "     " + rs.getString("professor_id"));

            // Step 2: Take new professor id
            System.out.print("Enter new Professor ID: ");
            String newProfId = sc.nextLine();

            // Step 3: Validate professor exists
            ps = Run.con.prepareStatement(
                    "SELECT name FROM professors WHERE professor_id = ?"
            );
            ps.setString(1, newProfId);
            ResultSet rs2 = ps.executeQuery();

            if (!rs2.next()) {
                System.out.println("Professor not found");
                return;
            }

            String profName = rs2.getString("name");

            // Step 4: Update course with new professor
            PreparedStatement update = Run.con.prepareStatement(
                    "UPDATE courses SET professor_id = ?, professor = ? WHERE course_id = ?"
            );
            update.setString(1, newProfId);
            update.setString(2, profName);
            update.setString(3, course_code);

            if (update.executeUpdate() > 0) {
                System.out.println("Professor assigned successfully");
            } else {
                System.out.println("Update failed");
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }

    }
    private void manageStudent() {
        try {
            int width = 20;
            System.out.println("\nSTUDENTS TABLE:\n");

            PreparedStatement ps1 = Run.con.prepareStatement("SELECT * FROM students");
            ResultSet rs1 = ps1.executeQuery();
            java.sql.ResultSetMetaData md1 = rs1.getMetaData();
            int colCount1 = md1.getColumnCount();
            for (int i = 1; i <= colCount1; i++) {
                System.out.printf("%-" + width + "s", md1.getColumnName(i));
            }
            System.out.println();


            for (int i = 0; i < colCount1 * width; i++) System.out.print("-");
            System.out.println();

            boolean hasStudents = false;

            while (rs1.next()) {
                hasStudents = true;
                for (int i = 1; i <= colCount1; i++) {
                    String val = rs1.getString(i);
                    if (val == null) val = "NULL";
                    System.out.printf("%-" + width + "s", val);
                }
                System.out.println();
            }

            if (!hasStudents) {
                System.out.println("No students found");
            }


            System.out.println("\nGPA TABLE:\n");

            PreparedStatement ps2 = Run.con.prepareStatement("SELECT * FROM gpa");
            ResultSet rs2 = ps2.executeQuery();
            java.sql.ResultSetMetaData md2 = rs2.getMetaData();
            int colCount2 = md2.getColumnCount();

            // Headers
            for (int i = 1; i <= colCount2; i++) {
                System.out.printf("%-" + width + "s", md2.getColumnName(i));
            }
            System.out.println();

            // Separator
            for (int i = 0; i < colCount2 * width; i++) System.out.print("-");
            System.out.println();

            boolean hasGPA = false;

            while (rs2.next()) {
                hasGPA = true;
                for (int i = 1; i <= colCount2; i++) {
                    String val = rs2.getString(i);
                    if (val == null) val = "NULL";
                    System.out.printf("%-" + width + "s", val);
                }
                System.out.println();
            }

            if (!hasGPA) {
                System.out.println("No GPA records found");
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
        /*Administrators should be able to view and update student records, grades, and
personal information.*/
        System.out.println("\n\n");
        System.out.println("Select:");
        System.out.println("1. update gpa");
        System.out.println("2. update semester");
        System.out.println("3. Exit");
        while(true){
            switch(Integer.parseInt(sc.nextLine())){
            case 1:updategpa();break;
            case 2:updatesem();break;
            case 3:return;
            }
        }
    }
    private void updatesem() {
        while(true) {
            try {
                System.out.println("Enter student id: ");
                String Student_id = sc.nextLine();
                System.out.println("Enter new sem: ");
                int sem = Integer.parseInt(sc.nextLine());
                PreparedStatement ps = Run.con.prepareStatement("update students set semester = ? where student_id = ?");
                ps.setInt(1, sem);
                ps.setString(2, Student_id);
                int x = ps.executeUpdate();
                if (x == 0) {
                    System.out.println("update failed.\n Retry....");
                } else {
                    System.out.println("Update Successfull");
                    return;
                }

            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }
    }
    void updategpa(){
        int sem=-1;
        while(sem!=0) {
            try {
                System.out.println("Enter student Id: ");
                String student_id = sc.nextLine();
                System.out.print("Enter Semester(enter 0 to exit): ");
                sem = Integer.parseInt(sc.nextLine());
                if(sem==0)return;
                System.out.println("Enter Sgpa: ");
                float sgpa = Float.valueOf(sc.nextLine());
                PreparedStatement ps = Run.con.prepareStatement("Update gpa set sem"+sem+" =?"+" where student_id=?");
                ps.setFloat(1, sgpa);
                ps.setString(2, student_id);
                int x = ps.executeUpdate();
                if (x != 0) {
                    System.out.println("Update Successfull");
                    break;
                } else {
                    System.out.println("Update failed\n \t Retry....");
                }
            }
            catch(NumberFormatException e){
                System.out.println("Enter a valid  Number..");
                continue;
            }
            catch(Exception e){
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }
        try {
            String query =
                    "UPDATE students s " +
                            "JOIN gpa g ON s.student_id = g.student_id " +
                            "SET s.cgpa = g.cgpa";

            PreparedStatement ps = Run.con.prepareStatement(query);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }

        }
    }



