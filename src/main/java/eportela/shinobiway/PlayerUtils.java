package eportela.shinobiway;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {
    public static Player getOnlinePlayer(String playerName) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(playerName)) {
                return onlinePlayer;
            }
        }
        return null;
    }

    public static UUID getPlayer(String displayName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(displayName);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return offlinePlayer.getUniqueId();
        } else {
            return null;
        }
    }
}