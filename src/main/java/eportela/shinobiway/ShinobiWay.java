package eportela.shinobiway;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.LuckPerms;

public final class ShinobiWay extends JavaPlugin {
    public static final String plugin_version = "[shinobiWay1.0]: ";
    private static final Map<UUID, List<String>> invitationCodes = new HashMap<>();
    public static LuckPerms luckPermsAPI;

    public static MyManager myManager;

    public static void com_handler(Player target, String message, int errorCode)
    {
        MessageHandler msg = new MessageHandler(message, errorCode);
        msg.msg(target);
    }

    @Override
    public void onEnable() {
        //luckPermsAPI = LuckPermsProvider.get();
        MyManager myManager = new MyManager(this);
        DatabaseManager databaseManager = new DatabaseManager();
        getCommand("shinobiGroup").setExecutor(new ShinobiGroupCommand());
        com_handler(null, "Success initializing " + plugin_version, 0);
    }

    public static Map<UUID, List<String>> getInvitationCodes() {
        return invitationCodes;
    }

    @Override
    public void onDisable() {
        DatabaseManager.getInstance().close();
    }
}
