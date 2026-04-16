package RunnablePack;
import java.sql.*;
public class JDBCConnection {

    private static JDBCConnection inst;
    private static Connection connection;

    // private constructor
    private JDBCConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/SVNIT";
            String username = "root";
            String password = "1234pass";

            connection = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // method to get singleton instance
    public static JDBCConnection getInstance() {
        if (inst == null) {
            inst = new JDBCConnection();
        }
        return inst;
    }

    // method to get connection
    public Connection getConnection() {
        return connection;
    }
}