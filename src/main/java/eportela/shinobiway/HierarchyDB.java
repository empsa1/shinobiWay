package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class HierarchyDB {
    public static boolean tryKick(Player player, ShinobiGroup group, String[] args) {
        if (args == null) {
            return false;
        }
        if (args[1].length() == 0) {
            ShinobiWay.com_handler(player, "Invalid player name!", 1);
            return false;
        }
        if (group.getName().length() == 0) {
            ShinobiWay.com_handler(player, "Invalid group! Contact staff!", 1);
            return false;
        }
        if (!player.getUniqueId().toString().equals(group.getOwnerUUID().toString())) {
            ShinobiWay.com_handler(player, "You do not have permissions to kick players from " + group.getName(), 1);
            return false;
        }
        String groupName1 = ShinobiDB.getPlayerGroup(player.getUniqueId()).getName();
        System.out.println("group 1: " + groupName1);
        String groupName2 = ShinobiDB.getPlayerGroup(PlayerUtils.getPlayer(args[1])).getName();
        System.out.println("group 2: " + groupName2);
        if (!groupName1.equals(groupName2)) {
            ShinobiWay.com_handler(player, "You cant kick players that are not from your group ", 1);
            return false;
        }
        if (player.getUniqueId().toString().equals(PlayerUtils.getPlayer(args[1]).toString())) {
            ShinobiWay.com_handler(player, "You cannot kick yourself from your group!", 1);
            return false;
        }
        if (args[1].length() == 0) {
            ShinobiWay.com_handler(player, "That person is not apart of your group!", 1);
            return false;
        }
        UUID playerUUID = PlayerUtils.getPlayer(args[1]);
        try {
            String deletePlayerSql = "DELETE FROM shinobi_members WHERE player_uuid = ?";
            try (PreparedStatement deletePlayerStmt = DatabaseManager.getConnection().prepareStatement(deletePlayerSql)) {
                deletePlayerStmt.setString(1, playerUUID.toString());
                deletePlayerStmt.executeUpdate();
                ShinobiWay.com_handler(player, "You successfully kicked " + args[1] + " from " + group.getName(), 0);
                if (PlayerUtils.getOnlinePlayer(args[1]).getDisplayName().length() != 0) {
                    ShinobiWay.com_handler(PlayerUtils.getOnlinePlayer(args[1]), "You have been kicked from " + group.getName(), 0);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryDemote(Player player, ShinobiGroup group, String[] args) {
        if (args == null) {
            return false;
        }
        if (args[1].length() == 0) {
            ShinobiWay.com_handler(player, "Invalid argument!", 1);
            return false;
        }
        if (!(ShinobiDB.getPlayerGroup(player.getUniqueId()).getOwnerUUID().equals(player.getUniqueId()))) {
            ShinobiWay.com_handler(player, "You do not have permissions to do that!", 1);
            return false;
        }
        UUID targetUUID = PlayerUtils.getPlayer(args[1]);
        if (targetUUID.toString().length() == 0) {
            ShinobiWay.com_handler(player, "That player does not exist or is not online!", 1);
            return false;
        }
        if (!(ShinobiDB.getPlayerGroup(targetUUID).getName().equals(ShinobiDB.getPlayerGroup(player.getUniqueId()).getName()))) {
            ShinobiWay.com_handler(player, "That player is not apart of your group!", 1);
            return false;
        }
        if (player.getUniqueId().toString().equals(targetUUID)) {
            ShinobiWay.com_handler(player, "You cannot demote yourself!", 1);
            return false;
        }
        ShinobiRank newRank = ShinobiMember.getNextRank(ShinobiMember.getShinobiRank(targetUUID));
        try {
            String updateRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
            try (PreparedStatement updateRankStmt = DatabaseManager.getConnection().prepareStatement(updateRankSql)) {
                updateRankStmt.setInt(1, newRank.ordinal());
                updateRankStmt.setString(2, targetUUID.toString());
                updateRankStmt.executeUpdate();
                ShinobiWay.com_handler(player, "You have successfully demoted " + args[1], 0);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryPromote(Player player, ShinobiGroup group, String[] args) {
        if (args == null) {
            return false;
        }
        if (args[1].length() == 0) {
            ShinobiWay.com_handler(player, "Invalid argument!", 1);
            return false;
        }
        if (!(ShinobiDB.getPlayerGroup(player.getUniqueId()).getOwnerUUID().equals(player.getUniqueId()))) {
            ShinobiWay.com_handler(player, "You do not have permissions to do that!", 1);
            return false;
        }
        UUID targetUUID = PlayerUtils.getPlayer(args[1]);
        if (targetUUID.toString().length() == 0) {
            ShinobiWay.com_handler(player, "That player does not exist or is not online!", 1);
            return false;
        }
        if (!(ShinobiDB.getPlayerGroup(targetUUID).getName().equals(ShinobiDB.getPlayerGroup(player.getUniqueId()).getName()))) {
            ShinobiWay.com_handler(player, "That player is not apart of your group!", 1);
            return false;
        }
        if (player.getUniqueId().toString().equals(targetUUID)) {
            ShinobiWay.com_handler(player, "You cannot promote yourself!", 1);
            return false;
        }
        ShinobiRank newRank = ShinobiMember.getPreviousRank(ShinobiMember.getShinobiRank(targetUUID));
        try {
            String updateRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
            try (PreparedStatement updateRankStmt = DatabaseManager.getConnection().prepareStatement(updateRankSql)) {
                updateRankStmt.setInt(1, newRank.ordinal());
                updateRankStmt.setString(2, targetUUID.toString());
                updateRankStmt.executeUpdate();
                ShinobiWay.com_handler(player, "You have successfully promoted " + args[1], 0);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
