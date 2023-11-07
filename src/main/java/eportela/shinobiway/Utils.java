package eportela.shinobiway;

import org.bukkit.entity.Player;

public class Utils {
    public static void error_handler(Player player, int error)
    {
        String message = null;
        if (error == ErrorCode.TARGET_VOID.ordinal()) {
            message = "The player you tried to invite does not exist or is not online!";
        }
        if (error == ErrorCode.TARGET_HAS_GROUP.ordinal()) {
            message = "The player you tried to reach is already is a group";
        }
        if (message != null) {
            ShinobiWay.com_handler(player, message, 1);
        }
    }
}
