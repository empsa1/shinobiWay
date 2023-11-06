package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VillageDB {
    public static Village loadVillageByName(String villageName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM villages WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, villageName);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    Village village = new Village(villageName);
                    String kageUUID = resultSet.getString("kage");
                    if (kageUUID != null) {
                        VillageMember kage = new VillageMember(UUID.fromString(kageUUID), VillageRank.KAGE);
                        if (kage != null) {
                            village.setKageUUID(UUID.fromString(kageUUID));
                        }
                    }
                    return village;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Village not found
    }
    public static void setKage(Player newKage, String villageName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start a transaction

            // Check if there's currently a Kage in the village
            String selectPreviousKageSql = "SELECT kage FROM villages WHERE name = ?";
            String updateKageRankSql = "UPDATE village_members SET village_rank = ? WHERE player_uuid = ?";
            int newKageRank = VillageRank.KAGE.ordinal(); // Set the rank for the new Kage

            try (PreparedStatement selectPreviousKageStmt = conn.prepareStatement(selectPreviousKageSql);
                 PreparedStatement updateKageRankStmt = conn.prepareStatement(updateKageRankSql)) {

                // Set the village name for the SELECT statement
                selectPreviousKageStmt.setString(1, villageName);
                ResultSet previousKageResult = selectPreviousKageStmt.executeQuery();

                if (previousKageResult.next()) {
                    String previousKageUUID = previousKageResult.getString("kage");

                    // Update the rank of the previous Kage (if any)
                    updateKageRankStmt.setInt(1, VillageRank.GENIN.ordinal()); // Set the desired rank
                    updateKageRankStmt.setString(2, previousKageUUID);
                    updateKageRankStmt.executeUpdate();
                }

                // Update the Kage in the villages table
                String updateKageSql = "UPDATE villages SET kage = ? WHERE name = ?";
                try (PreparedStatement updateKageStmt = conn.prepareStatement(updateKageSql)) {
                    // Set the new Kage's UUID and the village name
                    updateKageStmt.setString(1, newKage.getUniqueId().toString());
                    updateKageStmt.setString(2, villageName);
                    updateKageStmt.executeUpdate();
                }

                // Update the rank of the new Kage
                updateKageRankStmt.setInt(1, newKageRank);
                updateKageRankStmt.setString(2, newKage.getUniqueId().toString());
                updateKageRankStmt.executeUpdate();

                // Commit the transaction
                conn.commit();
            } catch (SQLException e) {
                // Handle any database-related errors
                conn.rollback(); // Rollback the transaction if an error occurs
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle connection-related errors
        }
    }

    public static void addVillage(Village village) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO villages (name) VALUES (?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, village.getName());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean villageAlreadyExists(String villageName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM villages WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, villageName);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // Return false by default (if there was an error)
    }

    public static void displayBank(Village playerVillage) {
    }
}
