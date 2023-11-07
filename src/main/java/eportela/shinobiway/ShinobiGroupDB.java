package eportela.shinobiway;

import com.google.common.hash.HashingOutputStream;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class ShinobiGroupDB {

    public static ShinobiGroup getGroup(String groupName) {
        try {
            String ownerSql = "SELECT group_owner FROM shinobi_groups WHERE group_name = ?";
            try (PreparedStatement ownerStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(ownerSql)) {
                ownerStmt.setString(1, groupName);
                ResultSet ownerResult = ownerStmt.executeQuery();
                if (ownerResult.next()) {
                    String ownerUUID = ownerResult.getString("group_owner");
                    // Create a new Group with the village name and the Kage's UUID
                    ShinobiGroup group = new ShinobiGroup(groupName, UUID.fromString(ownerUUID), GroupType.PARTNERSHIP.ordinal());
                    return group;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Group not found or an error occurred
    }
    public static boolean displayBank(Player player, ShinobiGroup group) {
        String groupName = group.getName();
        try {
            String sql = "SELECT group_balance FROM shinobi_groups WHERE group_name = ?";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, groupName);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    int balance = resultSet.getInt("group_balance");
                    ShinobiWay.com_handler(player, groupName + "balance: " + balance + " Ryo", 0);
                    return true;
                }
            }
        } catch (SQLException e) {
            ShinobiWay.com_handler(null, "There was a problem retrieving the bank balance of: " + groupName, 1);
            e.printStackTrace();
        }
        ShinobiWay.com_handler(player, "Something went wrong!", 1);
        return false;
    }

    public static boolean setGroupOwner(Player player, ShinobiGroup group, String[] args) {
        if (args[1].length() == 0) {
            Utils.error_handler(player, ErrorCode.INVALID_TARGET.ordinal());
            return false;
        }
        String groupName = group.getName();
        Player newOwner = PlayerUtils.getOnlinePlayer(args[1]);
        if (newOwner == null) {
            Utils.error_handler(player, ErrorCode.TARGET_VOID.ordinal());
        }
        try {
            ShinobiWay.databaseManager.getConnection().setAutoCommit(false); // Start a transaction
            try {
                // Find the current owner of the group
                String selectCurrentOwnerSql = "SELECT group_owner FROM shinobi_groups WHERE group_name = ?";
                try (PreparedStatement selectCurrentOwnerStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(selectCurrentOwnerSql)) {
                    selectCurrentOwnerStmt.setString(1, groupName);
                    ResultSet currentOwnerResult = selectCurrentOwnerStmt.executeQuery();

                    if (currentOwnerResult.next()) {
                        String currentOwnerUUID = currentOwnerResult.getString("kage");

                        // Update the rank of the previous owner (if any) to GENIN
                        String updatePreviousOwnerRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
                        try (PreparedStatement updatePreviousOwnerRankStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(updatePreviousOwnerRankSql)) {
                            updatePreviousOwnerRankStmt.setInt(1, ShinobiRank.GENIN.ordinal()); // Set the desired rank
                            updatePreviousOwnerRankStmt.setString(2, currentOwnerUUID);
                            updatePreviousOwnerRankStmt.executeUpdate();
                        }
                    }

                    // Update the new owner's rank to KAGE
                    String updateNewOwnerRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
                    try (PreparedStatement updateNewOwnerRankStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(updateNewOwnerRankSql)) {
                        updateNewOwnerRankStmt.setInt(1, ShinobiRank.KAGE.ordinal());
                        updateNewOwnerRankStmt.setString(2, newOwner.getUniqueId().toString());
                        updateNewOwnerRankStmt.executeUpdate();
                    }

                    // Update the owner of the group in the villages table
                    String updateOwnerSql = "UPDATE shinobi_groups SET group_owner = ? WHERE group_name = ?";
                    try (PreparedStatement updateOwnerStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(updateOwnerSql)) {
                        updateOwnerStmt.setString(1, newOwner.getUniqueId().toString());
                        updateOwnerStmt.setString(2, groupName);
                        updateOwnerStmt.executeUpdate();
                    }

                    ShinobiWay.databaseManager.getConnection().commit(); // Commit the transaction
                } catch (SQLException e) {
                    ShinobiWay.databaseManager.getConnection().rollback(); // Rollback the transaction if an error occurs
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryDisband(Player player, ShinobiGroup group) {
        String groupName = group.getName();
        try {
            // Delete the group from the Group table
            String deleteGroupSql = "DELETE FROM shinobi_groups WHERE group_name = ?";
            try (PreparedStatement deleteGroupStmt = ShinobiWay.databaseManager.getConnection().prepareStatement(deleteGroupSql)) {
                deleteGroupStmt.setString(1, groupName);
                deleteGroupStmt.executeUpdate();
                ShinobiWay.com_handler(player, "You have successfully disbanded " + groupName, 0);
            }
            // Delete any related entries in other tables (e.g., Diplomacy)
            // Add code to delete associated data in other tables if needed
        } catch (SQLException e) {
            ShinobiWay.com_handler(player, "Something went wrong!", 1);
            e.printStackTrace();
        }
        ShinobiWay.com_handler(null, "There was a problem disbanding: " + groupName, 1);
        return false;
    }

    public static boolean addGroup(Player player, String groupName, UUID ownerUUID, GroupType groupType) {
        if (groupName == null || groupName.length() == 0 || ownerUUID == null || groupType.ordinal() < 0) {
            return false;
        }
        try {
            String sql = "INSERT INTO shinobi_groups (group_name, group_owner, group_type) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, groupName);
                pstmt.setString(2, ownerUUID.toString());
                pstmt.setInt(3, groupType.ordinal());
                pstmt.executeUpdate();
                ShinobiWay.com_handler(player, "You Successfully created your new partnership: " + groupName, 0);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createGroup(Player player, String[] args) {
        if (args[1].length() == 0) {
            Utils.error_handler(player, ErrorCode.USAGE.ordinal());
            return false;
        }
        String groupName = args[1];
        if (ShinobiDB.getPlayerGroup(player) == null) {
            ShinobiGroup group = new ShinobiGroup(groupName, player.getUniqueId(), GroupType.PARTNERSHIP.ordinal());
            if (addGroup(player, group.getName(), player.getUniqueId(), GroupType.PARTNERSHIP) == false) {
                return false;
            }
            else {
               return (ShinobiDB.addPlayerToGroup(player, groupName));
            }
        }
        else {
            Utils.error_handler(player, ErrorCode.USER_ALREADY_GROUP.ordinal());
            return false;
        }
    }
}
