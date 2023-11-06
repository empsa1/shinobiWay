package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {
    private static Connection connection;

    public DatabaseManager() {
        try {
            // Register the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            String url = "jdbc:mysql://localhost:3306/shinobiway";
            String username = "shinobiWay";
            //String password = "pluginmanager";

            connection = DriverManager.getConnection(url, username, null);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle connection errors
        }
    }



    public static Connection getConnection() {
        return connection;
    }
}