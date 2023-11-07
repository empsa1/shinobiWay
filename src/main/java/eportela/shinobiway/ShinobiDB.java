package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ShinobiDB {
    public static boolean removePlayerFromGroup(Player player, ShinobiGroup group) {
        if (ShinobiGroupDB.getGroup(group.getName()) == null) {
            Utils.error_handler(player, ErrorCode.TARGET_NO_GROUP.ordinal());
            return false;
        }
        String playerUUID = player.getUniqueId().toString();
        try {
            String sql = "DELETE FROM shinobi_members WHERE player_uuid = ?";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, playerUUID);
                int rowsAffected = pstmt.executeUpdate();
                ShinobiWay.com_handler(player, "You successfully left " + group, 0);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            ShinobiWay.com_handler(null, "Something failed deleting an a player entry from the database. The process was canceled", 0);
            e.printStackTrace();
        }
        ShinobiWay.com_handler(player, "Something went wrong!", 0);
        return false;
    }

    public static boolean addPlayerToGroup(Player player, String groupName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO shinobi_members (group_name, shinobi_rank, player_uuid) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, groupName);
                pstmt.setInt(2, ShinobiRank.GENIN.ordinal());
                pstmt.setString(3, player.getUniqueId().toString());

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("A new Shinobi Member was added successfully.");
                    return true;
                } else {
                    System.out.println("Failed to add a new Shinobi Member.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ShinobiRank getRank(Player player) {
        UUID playerUUID = player.getUniqueId();
        try {
            String sql = "SELECT shinobi_rank FROM shinobi_members WHERE player_uuid = ?";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, playerUUID.toString());
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    int rankValue = resultSet.getInt("shinobi_rank");
                    return ShinobiRank.values()[rankValue];
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ShinobiGroup getPlayerGroup(Player player) {
        UUID playerUUID = player.getUniqueId();
        try {
            String sql = "SELECT group_name FROM shinobi_members WHERE player_uuid = ?";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, playerUUID.toString());
                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    String groupName = resultSet.getString("group_name");
                    // Here you can map the group name to your Group enum
                    System.out.println("getPlayerGroup(): " + ShinobiGroupDB.getGroup(groupName));
                    return (ShinobiGroupDB.getGroup(groupName)); // Assuming you have a Group enum
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getPlayerGroup(): Going to print null");
        return null; // Player not found or error occurred
    }
}
