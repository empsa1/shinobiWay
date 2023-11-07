package eportela.shinobiway;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class ShinobiWay extends JavaPlugin {
    public static final String plugin_version = "[shinobiWay1.0]: ";
    public static DatabaseManager databaseManager;

    public static void com_handler(Player target, String message, int errorCode)
    {
        MessageHandler msg = new MessageHandler(message, errorCode);
        msg.msg(target);
    }

    @Override
    public void onEnable() {
        databaseManager = new DatabaseManager();
        getCommand("shinobiGroup").setExecutor(new ShinobiGroupCommand());
        com_handler(null, "Success initializing " + plugin_version, 0);
    }

    @Override
    public void onDisable() {
        try {
            if (databaseManager != null) {
                Connection conn = DatabaseManager.getConnection();
                if (conn != null) {
                    conn.close();
                    com_handler(null, "Success terminating " + plugin_version, 0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
