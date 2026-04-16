package RunnablePack;
import All.Webpages.*;
import All.Factory.*;
import All.Exceptions.*;
import java.sql.*;
import java.util.Scanner;
import RunnablePack.JDBCConnection;
public class Run {
    public static Connection con=null;
    public static Statement stmt=null;
    public static void main() {
        try {
            JDBCConnection inst = JDBCConnection.getInstance();
            con = inst.getConnection();
        }
         catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*********************************** University HOMEPAGE ***************************\n");
        System.out.println("======================================== MIS ===================================");
        Scanner sc = new Scanner(System.in);
        int choice=0;

        String type= null;
        while(true) {
            try {
                 System.out.print("\n 1: Sign up \n 2: Log in \n 3:Exit application\n Enter: ");
                choice = Integer.parseInt(sc.nextLine());
                if(choice >3 ){
                    System.out.println("Enter a valid Choice");
                    continue;
                }
                if (choice == 3) {
                    System.out.println("Exit..");
                    System.exit(0);
                }
                System.out.print(" 1: Student \n 2: Professor \n 3: Administrator  \n 4:Teaching Assistant \n  Enter:");
                type = null;
                switch (Integer.parseInt(sc.nextLine())) {
                    case 1:
                        type = "Student";
                        break;
                    case 2:
                        type = "Professor";
                        break;
                    case 3:
                        type = "Administrator";
                        break;
                    case 4:
                        type = "TeachingAssistant";
                        break;
                    default:
                        System.out.println("Error,invalid choice: ");
                        continue;

                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please Enter Valid input");
                continue;
            }
        }
        if(choice == 2){
            try {
                System.out.println("**************************************** Login *********************************************");
                int attempts = 3;
                String Id=null;
                String password=null;
                while(attempts>0) {
                    try {
                        System.out.print("Enter Id: ");
                        Id = sc.nextLine();
                        System.out.print("Enter Password: ");
                        password = sc.nextLine();
                        String table = type + "s";
                        String query = "SELECT * FROM " + table + " WHERE "+type+"_id = ? AND Password = ?";
                        PreparedStatement pstmt = con.prepareStatement(query);
                        pstmt.setString(1, Id);
                        pstmt.setString(2, password);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            System.out.println("Access Granted");
                            break;
                        } else {
                            attempts--;
                            throw new InvalidLoginException();
                        }
                    } catch (SQLException | InvalidLoginException e) {
                        System.out.println(e);
                    } catch (Exception e){
                        System.out.println("An unknown error occurred : at 99 of Run.java : "+e);
                    }
                }
                if(attempts==0){
                    System.out.println("Trials exhausted...");
                    return;
                }
                Page p= PageFactory.getPage(type,Id);
                p.main();
            }
            catch(Exception e){
                System.out.println(e);
            }

        }
        else if(choice == 1) {
            while (true) {
                try {

                    System.out.println("**************************************** Sign Up *********************************************");

                    System.out.print("Enter Id: ");
                    String id = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    String table = type + "s";
                    String query = "INSERT INTO " + table + " (" + type + "_id, password, name) VALUES (?, ?, ?)";

                    PreparedStatement pstmt = con.prepareStatement(query);


                    pstmt.setString(1, id);
                    pstmt.setString(2, password);
                    pstmt.setString(3, name);
                    pstmt.executeUpdate();
                    System.out.println("                 Sign up Successful               ");
                    Page p = PageFactory.getPage(type, id);
                    p.main();
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("This user has already Logged In \n retry..");
                } catch (Exception e) {
                    System.out.println("An unknown error occurred : at 142 of Run.java : "+e);
                }
                break;
            }

        }
        else{
            System.out.println("Invalid");
        }

    }
}
