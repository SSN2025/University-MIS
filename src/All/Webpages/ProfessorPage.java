package All.Webpages;
import RunnablePack.Run;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;


public class ProfessorPage implements Page {
    Scanner sc = new Scanner(System.in);
    Statement stmt = null;
    ResultSet rs = null;
    String prof_Id = null;

    public ProfessorPage(String id) {
        this.prof_Id = id;
        if (Run.con == null) {
            Run.main();
            System.exit(-1);
        }
    }

    public void showInfo() {
        System.out.println("********************* Professor Page ******************************");
        System.out.println("Following are the keys: ");
        System.out.println("1. Manage Courses");
        System.out.println("2. View Enrolled Students");
        System.out.println("3. See Feedback of a course");
        System.out.println("4. Exit Website");
    }


    public void main() {
        while(true) {
            try {
                this.showInfo();
                System.out.print("Enter :");
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        manage();
                        break;
                    case 2:
                        ViewStd();
                        break;
                    case 3:
                        see_feedback();
                        break;
                    case 4:
                        System.exit(-1);
                        break;
                    default:
                        System.out.println("Enter a valid Choice");
                        continue;

                }
            }catch (NumberFormatException e){
                System.out.println("Invalid Input");
                continue;
            }
        }

    }

    void manage() {
        while (true) {
            System.out.println("Enter course_code: ");
            String course_code = sc.nextLine();
            try {
                PreparedStatement ps = Run.con.prepareStatement("Select professor_id from courses where course_id = ?");
                ps.setString(1, course_code);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (!rs.getString("professor_id").equals(this.prof_Id)) {
                        System.out.println("You are not allowed to manage this course.");
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
            System.out.println("Choose, to update:");
            System.out.println("1. Syllabus");
            System.out.println("2. Timing");
            System.out.println("3. Credits");
            System.out.println("4. Prerequisites");
            System.out.println("5. Batch Size");

            System.out.print("Enter: ");
            int val = Integer.parseInt(sc.nextLine());
            switch (val) {
                case 1:
                    update_syll(course_code);
                    break;
                case 2:
                    update_timings(course_code);
                    break;
                case 3:
                    update_credits(course_code);
                    break;
                case 4:
                    update_prerequisites(course_code);
                    break;
                case 5:
                    update_batchSize(course_code);
                    break;
                default:
                    System.out.println("Enter a valid number..");
                    continue;
            }
            break;
        }
    }
    private void update_prerequisites(String course_code) {
        while (true) {
            try {
                PreparedStatement ps = Run.con.prepareStatement(
                        "SELECT prerequisites FROM courses WHERE course_id = ?"
                );
                ps.setString(1, course_code);
                rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("Current Prerequisites: ");
                    System.out.println(rs.getString("prerequisites"));
                    break;
                } else {
                    System.out.println("Enter a valid Course");
                    return;
                }
            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }

        System.out.println("Enter new prerequisites:");
        try {
            PreparedStatement ps = Run.con.prepareStatement(
                    "UPDATE courses SET prerequisites = ? WHERE course_id = ?"
            );
            ps.setString(1, sc.nextLine());
            ps.setString(2, course_code);

            if (ps.executeUpdate() != 0) {
                System.out.println("Prerequisites Updated Successfully");
            } else {
                System.out.println("Updation failed");
            }
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    private void update_credits(String course_code) {
        while (true) {
            try {
                PreparedStatement ps = Run.con.prepareStatement(
                        "SELECT credits FROM courses WHERE course_id = ?"
                );
                ps.setString(1, course_code);
                rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("Current Credits: ");
                    System.out.println(rs.getInt("credits"));
                    break;
                } else {
                    System.out.println("Enter a valid Course");
                    return;
                }
            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }

        System.out.println("Enter new credits:");
        try {
            PreparedStatement ps = Run.con.prepareStatement(
                    "UPDATE courses SET credits = ? WHERE course_id = ?"
            );
            ps.setInt(1, sc.nextInt());
            sc.nextLine();
            ps.setString(2, course_code);

            if (ps.executeUpdate() != 0) {
                System.out.println("Credits Updated Successfully");
            } else {
                System.out.println("Updation failed");
            }
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }

    }
    private void update_timings(String course_code) {
        while (true) {
            try {
                PreparedStatement ps = Run.con.prepareStatement(
                        "SELECT schedule FROM courses WHERE course_id = ?"
                );
                ps.setString(1, course_code);
                rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("Current Timings: ");
                    System.out.println(rs.getString("schedule"));
                    break;
                } else {
                    System.out.println("Enter a valid Course");
                    return;
                }
            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }

        System.out.println("Enter new timings:");
        try {
            PreparedStatement ps = Run.con.prepareStatement(
                    "UPDATE courses SET schedule = ? WHERE course_id = ?"
            );
            ps.setString(1, sc.nextLine());
            ps.setString(2, course_code);

            if (ps.executeUpdate() != 0) {
                System.out.println("Timings Updated Successfully");
            } else {
                System.out.println("Updation failed");
            }
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    private void update_syll(String course_code) {
        while (true) {
            try {

                PreparedStatement ps = Run.con.prepareStatement("Select syllabus from courses where course_id = ?");
                ps.setString(1, course_code);
                rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("Current Syllabus: ");
                    System.out.println(rs.getString("syllabus"));
                    break;
                } else {
                    System.out.println("Enter a valid Course");
                }
            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }
        System.out.println("Enter new syllabus:");
        try {
            PreparedStatement ps = Run.con.prepareStatement("Update courses set syllabus = ? where course_id = ?");
            ps.setString(1, sc.nextLine());
            ps.setString(2, course_code);
            if (ps.executeUpdate() != 0) {
                System.out.println("Syllabus Updated Successfully");
            } else {
                System.out.println("Updation failed");
            }
        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    private void update_batchSize(String course_code) {

        // Step 1: Validate course and show current BatchSize
        while (true) {
            try {
                PreparedStatement ps = Run.con.prepareStatement(
                        "SELECT BatchSize FROM courses WHERE course_id = ?"
                );
                ps.setString(1, course_code);
                rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.print("Current Batch Size: ");
                    System.out.println(rs.getInt("BatchSize"));
                    break;
                } else {
                    System.out.println("Enter a valid Course");
                    return; // avoid infinite loop if invalid
                }

            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }


        System.out.print("Enter new Batch Size: ");
        int newSize = sc.nextInt();
        sc.nextLine();

        try {
            PreparedStatement ps = Run.con.prepareStatement(
                    "UPDATE courses SET BatchSize = ? WHERE course_id = ?"
            );
            ps.setInt(1, newSize);
            ps.setString(2, course_code);

            if (ps.executeUpdate() != 0) {
                System.out.println("Batch Size Updated Successfully");
            } else {
                System.out.println("Updation failed");
            }

        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
    void ViewStd() {
        while (true) {
            System.out.println("Enter course_code: ");
            String course_code = sc.nextLine();

            try {
                // Step 1: Check if course exists
                PreparedStatement check = Run.con.prepareStatement(
                        "SELECT course_id FROM courses WHERE course_id = ?"
                );
                check.setString(1, course_code);
                ResultSet rs1 = check.executeQuery();

                if (!rs1.next()) {
                    System.out.println("Invalid Course");
                    continue;
                }

                // Step 2: Fetch enrolled students
                PreparedStatement ps = Run.con.prepareStatement(
                        "SELECT Student_id FROM enrollments WHERE course_id = ?"
                );
                ps.setString(1, course_code);
                rs = ps.executeQuery();


                if (!rs.next()) {
                    System.out.println("No students enrolled");
                } else {
                       System.out.println("Enrolled Students:");
                    do {
                        System.out.println(rs.getString("Student_id") + " ");
                    } while (rs.next());
                }

                break;

            } catch (Exception e) {
                System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
            }
        }
    }
    void see_feedback() {
    while (true) {
        System.out.println("Enter course_code: ");
        String course_code = sc.nextLine();
        try {
            PreparedStatement ps = Run.con.prepareStatement("Select professor_id from courses where course_id = ?");
            ps.setString(1, course_code);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (!rs.getString("professor_id").equalsIgnoreCase(this.prof_Id)) {
                    System.out.println("You are not allowed to manage this course.");
                    continue;
                }
            }
            ps = Run.con.prepareStatement("Select student_id, feedback from feedback where course_id = ?");
            ps.setString(1,course_code);
            rs = ps.executeQuery();
            int w1 = 20;
            int w2 = 100;
            System.out.printf("%-" + w1 + "s%-" + w2 + "s\n", "student_id", "feedback");
            for (int i = 0; i < w1 + w2; i++) System.out.print("-");
            System.out.println();
            boolean found = false;
            while (rs.next()) {
                found = true;

                String studentId = rs.getString("student_id");
                String feedback = rs.getString("feedback");

                if (studentId == null) studentId = "NULL";
                if (feedback == null) feedback = "NULL";

                System.out.printf("%-" + w1 + "s%-" + w2 + "s\n", studentId, feedback);
            }

            if (!found) {
                System.out.println("No feedback found for this course");
            }


        } catch (Exception e) {
            System.out.println("An unknown error occurred at line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + " in file " + Thread.currentThread().getStackTrace()[1].getFileName() + " : " + e);
        }
    }
}
}



/*Professors should be able to view and manage their courses, including updating
course details like syllabus, class timings, credits, prerequisites, enrollment limits,
office hours, and anything else if required.
Note: The professor can only read and update the course. (Adding and deleting
the course is handled by admin)*/