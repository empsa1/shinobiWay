package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class HierarchyDB {
    public static boolean tryKick(Player player, ShinobiGroup group, String[] args) {
        UUID playerUUID = player.getUniqueId();
        try  {
            String deletePlayerSql = "DELETE FROM shinobi_members WHERE player_uuid = ?";
            try (PreparedStatement deletePlayerStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(deletePlayerSql)) {
                deletePlayerStmt.setString(1, playerUUID.toString());
                deletePlayerStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryDemote(Player player, ShinobiGroup group, String[] args) {
        UUID playerUUID = player.getUniqueId();
        ShinobiRank newRank = ShinobiMember.getNextRank(ShinobiMember.getShinobiRank(playerUUID));
        try {
            String updateRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
            try (PreparedStatement updateRankStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(updateRankSql)) {
                updateRankStmt.setInt(1, newRank.ordinal());
                updateRankStmt.setString(2, playerUUID.toString());
                updateRankStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryPromote(Player player, ShinobiGroup group, String[] args) {
        UUID playerUUID = player.getUniqueId();
        ShinobiRank newRank = ShinobiMember.getPreviousRank(ShinobiMember.getShinobiRank(playerUUID));
        try {
            String updateRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
            try (PreparedStatement updateRankStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(updateRankSql)) {
                updateRankStmt.setInt(1, newRank.ordinal());
                updateRankStmt.setString(2, playerUUID.toString());
                updateRankStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
