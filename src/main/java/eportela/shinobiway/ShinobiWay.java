package eportela.shinobiway;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShinobiWay extends JavaPlugin {
    public static final String plugin_version = "[shinobiWay1.0]: ";

    public static void communication_handler(Player target, String message, int errorCode)
    {
        MessageHandler msg = new MessageHandler(message, errorCode);
        msg.msg(target);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
