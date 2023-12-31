package eportela.shinobiway;

import com.google.common.hash.HashingOutputStream;
import org.bukkit.entity.Player;
import org.eclipse.jetty.server.session.DatabaseAdaptor;

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
            try (PreparedStatement ownerStmt = DatabaseManager.getConnection().prepareStatement(ownerSql)) {
                ownerStmt.setString(1, groupName);
                ResultSet ownerResult = ownerStmt.executeQuery();
                if (ownerResult.next()) {
                    String ownerUUID = ownerResult.getString("group_owner");
                    ShinobiGroup group = new ShinobiGroup(groupName, UUID.fromString(ownerUUID), GroupType.PARTNERSHIP.ordinal());
                    return group;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean setGroupOwner(Player player, ShinobiGroup group, String[] args) {
        if (args == null) {
            return false;
        }
        if (!player.getUniqueId().toString().equals(group.getOwnerUUID().toString())) {
            ShinobiWay.com_handler(player, "You do not have permissions to change the leader of " + group.getName(), 1);
            return false;
        }
        if (player.getUniqueId().toString().equals(PlayerUtils.getPlayer(args[1]).toString())) {
            ShinobiWay.com_handler(player, "You are already the owner of " + group.getName(), 0);
            return false;
        }
        if (args[1].length() == 0) {
            ShinobiWay.com_handler(player, "Invalid player!", 1);
            return false;
        }
        String groupName = group.getName();
        UUID newOwner = PlayerUtils.getPlayer(args[1]);
        if (newOwner.toString().length() == 0) {
            ShinobiWay.com_handler(player, "Invalid player!", 1);
            return false;
        }
        String groupName1 = ShinobiDB.getPlayerGroup(player.getUniqueId()).getName();
        System.out.println("group 1: " + groupName1);
        String groupName2 = ShinobiDB.getPlayerGroup(PlayerUtils.getPlayer(args[1])).getName();
        System.out.println("group 2: " + groupName2);
        if (!groupName1.equals(groupName2)) {
            ShinobiWay.com_handler(player, "You cant give ownership to players that are not from your group ", 1);
            return false;
        }
        try {
            String selectCurrentOwnerSql = "SELECT group_owner FROM shinobi_groups WHERE group_name = ?";
            try (PreparedStatement selectCurrentOwnerStmt = DatabaseManager.getConnection().prepareStatement(selectCurrentOwnerSql)) {
                selectCurrentOwnerStmt.setString(1, groupName);
                ResultSet currentOwnerResult = selectCurrentOwnerStmt.executeQuery();
                if (currentOwnerResult.next()) {
                    String currentOwnerUUID = currentOwnerResult.getString("group_owner");
                    String updatePreviousOwnerRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
                    try (PreparedStatement updatePreviousOwnerRankStmt = DatabaseManager.getConnection().prepareStatement(updatePreviousOwnerRankSql)) {
                        updatePreviousOwnerRankStmt.setInt(1, ShinobiRank.GENIN.ordinal()); // Set the desired rank
                        updatePreviousOwnerRankStmt.setString(2, currentOwnerUUID);
                        updatePreviousOwnerRankStmt.executeUpdate();
                    }
                }
                String updateNewOwnerRankSql = "UPDATE shinobi_members SET shinobi_rank = ? WHERE player_uuid = ?";
                try (PreparedStatement updateNewOwnerRankStmt = DatabaseManager.getConnection().prepareStatement(updateNewOwnerRankSql)) {
                    updateNewOwnerRankStmt.setInt(1, ShinobiRank.KAGE.ordinal());
                    updateNewOwnerRankStmt.setString(2, newOwner.toString());
                    updateNewOwnerRankStmt.executeUpdate();
                }
                String updateOwnerSql = "UPDATE shinobi_groups SET group_owner = ? WHERE group_name = ?";
                try (PreparedStatement updateOwnerStmt = DatabaseManager.getConnection().prepareStatement(updateOwnerSql)) {
                    updateOwnerStmt.setString(1, newOwner.toString());
                    updateOwnerStmt.setString(2, groupName);
                    updateOwnerStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ShinobiWay.com_handler(player, "You have successfully declared " + args[1] + " as the new leader of " + groupName, 0);
        return true;
    }
    public static boolean tryDisband(Player player, ShinobiGroup group) {
        System.out.println("Inside try disband");
        String groupName = group.getName();
        if (!group.getOwnerUUID().toString().equals(player.getUniqueId().toString())) {
            ShinobiWay.com_handler(player, "You do not have the permissions to disband " + groupName, 1);
            return false;
        }
        try {
            String deleteDiplomacySql = "DELETE FROM diplomacy WHERE group_name1 = ? OR group_name2 = ?";
            try (PreparedStatement deleteDiplomacyStmt = DatabaseManager.getConnection().prepareStatement(deleteDiplomacySql)) {
                deleteDiplomacyStmt.setString(1, groupName);
                deleteDiplomacyStmt.setString(2, groupName);
                deleteDiplomacyStmt.executeUpdate();
            }
            String deleteMembersSql = "DELETE FROM shinobi_members WHERE group_name = ?";
            try (PreparedStatement deleteMembersStmt = DatabaseManager.getConnection().prepareStatement(deleteMembersSql)) {
                deleteMembersStmt.setString(1, groupName);
                int deletedMembers = deleteMembersStmt.executeUpdate();
                String deleteGroupSql = "DELETE FROM shinobi_groups WHERE group_name = ?";
                try (PreparedStatement deleteGroupStmt = DatabaseManager.getConnection().prepareStatement(deleteGroupSql)) {
                    deleteGroupStmt.setString(1, groupName);
                    int deletedGroup = deleteGroupStmt.executeUpdate();
                    if (deletedMembers > 0 || deletedGroup > 0) {
                        ShinobiWay.com_handler(player, "You have successfully disbanded " + groupName + "! Fly free my bird!", 0);
                        return true;
                    }
                    ShinobiWay.com_handler(player, "How did we get here!", 1);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ShinobiWay.com_handler(player, "How did we get here!", 1);
        return false;
    }
    public static boolean addGroup(Player player, String groupName, UUID ownerUUID, GroupType groupType) {
        if (groupName == null || groupName.length() == 0 || ownerUUID == null || groupType.ordinal() < 0) {
            return false;
        }
        try {
            String sql = "INSERT INTO shinobi_groups (group_name, group_owner, group_type) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
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
            ShinobiWay.com_handler(player, "Invalid group name!", 1);
            return false;
        }
        String groupName = args[1];
        if (ShinobiDB.getPlayerGroup(player.getUniqueId()) == null) {
            ShinobiGroup group = new ShinobiGroup(groupName, player.getUniqueId(), GroupType.PARTNERSHIP.ordinal());
            if (addGroup(player, group.getName(), player.getUniqueId(), GroupType.PARTNERSHIP) == false) {
                return false;
            }
            else {
               return (ShinobiDB.addPlayerToGroup(player, groupName));
            }
        }
        else {
            ShinobiWay.com_handler(player, "You are already part of a group!", 1);
            return false;
        }
    }
}
