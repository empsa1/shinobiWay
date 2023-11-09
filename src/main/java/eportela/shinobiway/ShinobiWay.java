package eportela.shinobiway;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.LuckPerms;

public final class ShinobiWay extends JavaPlugin {
    public static final String plugin_version = "[shinobiWay1.0]: ";
    private static InviteList invitationCodes;

    private static Diplomacy diplomacyCodes;
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
        invitationCodes = new InviteList(new ArrayList<ArrayList<String>>(1));
        diplomacyCodes = new Diplomacy(new ArrayList<ArrayList<String>>(1));
        getCommand("shinobiGroup").setExecutor(new ShinobiGroupCommand());
        com_handler(null, "Success initializing " + plugin_version, 0);
    }

    public static InviteList getInvitationCodes() {
        return invitationCodes;
    }

    public static Diplomacy getDiplomacyCodes() {
        return diplomacyCodes;
    }

    @Override
    public void onDisable() {
        DatabaseManager.getInstance().close();
    }
}
