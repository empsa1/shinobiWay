package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.util.UUID;

public class ShinobiGroupUtils {
    public static boolean isOwnerGroup(UUID playerUUID) {
        ShinobiGroup group = ShinobiDB.getPlayerGroup(playerUUID);
        if (group == null || group.getName().length() == 0) {
            return false;
        }
       if (!playerUUID.toString().equals(group.getOwnerUUID().toString())) {
           return false;
       }
        return true;
    }
    public static boolean isMemberGroup(UUID playerUUID, String groupName) {
        return true;
    }
    public static boolean isHigherRank(UUID playerUUID, String groupName, ShinobiRank rank) {
        return true;
    }
    public static boolean messageGroup(String groupName, String message, int errorCode) {
        return true;
    }
}
