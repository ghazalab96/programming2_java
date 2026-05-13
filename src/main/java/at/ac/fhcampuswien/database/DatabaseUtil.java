package at.ac.fhcampuswien.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:h2:~/movieDb";
    private static final String USER = "user";
    private static final String PASSWORD = "pw";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS movies (
                    id UUID PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    genre VARCHAR(100) NOT NULL,
                    release_year INT NOT NULL
                );
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createTableSql);

        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize database", e);
        }
    }
}