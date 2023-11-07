package eportela.shinobiway;

import org.bukkit.entity.Player;

public class ShinobiGroupUtils {
    public static boolean sendInvite(String[] args, Player player, ShinobiGroup playerGroup) {
        Player invited = PlayerUtils.getOnlinePlayer(args[1]);
        return true;
    }
}
