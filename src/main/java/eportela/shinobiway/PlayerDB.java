package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDB {

    public static void removePlayerFromVillage(Player player) {
        try (Connection conn = DatabaseManager.getConnection()) {
            Village village = PlayerDB.getPlayerVillage(player.getUniqueId().toString());

            if (village != null) {
                // Check if the player is the Kage of the village
                if (village.getKageUUID().equals(player.getUniqueId())) {
                    player.sendMessage("You are the Kage of your village. You must pass on the role before leaving.");
                    return; // Prevent the player from leaving
                }

                String sql = "DELETE FROM village_members WHERE player_uuid = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, player.getUniqueId().toString());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addPlayerToVillageDB(Player player, Village village) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO village_members (player_uuid, village_name, village_rank) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, String.valueOf(player.getUniqueId()));
                pstmt.setString(2, village.getName());
                pstmt.setString(3, String.valueOf(VillageRank.GENIN));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isVillageMember(Player player) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Create a database connection
            conn = DatabaseManager.getConnection();
            // Define the SQL query to check if the player is a member of the specified village
            String query = "SELECT COUNT(*) FROM village_members WHERE player_uuid = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, String.valueOf(player.getUniqueId()));
            rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // If count is greater than 0, the player is a member; otherwise, they are not.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related exceptions here
        } finally {
            // Close the database resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle closing resources exceptions
            }
        }
        return false; // Return false if there was an issue with the database
    }
    public static boolean isPlayerKage(Player player) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT kage FROM villages WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, getPlayerVillage(player.getUniqueId().toString()).getName());
                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    String kageUUID = resultSet.getString("kage");
                    return kageUUID != null && kageUUID.equals(player.getUniqueId().toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static Village getPlayerVillage(String playerUUID) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT village_name FROM village_members WHERE player_uuid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, playerUUID);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    String villageName = resultSet.getString("village_name");
                    return VillageDB.loadVillageByName(villageName); // Assuming you have a method to load a village by name
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Player not found in any village
    }
}
