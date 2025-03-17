import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/devhubb"; // Database URL
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "CSworm123"; // MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Database connection failed");
        }
    }
}
