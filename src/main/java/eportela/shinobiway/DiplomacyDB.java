package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiplomacyDB {

    public static DiplomacyStatus getDiplomacyStatus(ShinobiGroup group1, ShinobiGroup group2) {
        String groupName1 = group1.getName();
        String groupName2 = group2.getName();
        try {
            String sql = "SELECT group_diplomacy FROM shinobi_groups WHERE (group_name = ? AND group_name = ?) OR (group_name = ? AND group_name = ?)";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, groupName1);
                pstmt.setString(2, groupName2);
                pstmt.setString(3, groupName2);
                pstmt.setString(4, groupName1);

                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    int statusValue = resultSet.getInt("status");
                    return DiplomacyStatus.values()[statusValue];
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DiplomacyStatus.NEUTRAL; // Default to NEUTRAL if status is not found or an error occurs
    }

    public static boolean setDiplomacy(ShinobiGroup group1, ShinobiGroup group2, DiplomacyStatus status) {
        String groupName1 = group1.getName();
        String groupName2 = group2.getName();
        int statusValue = status.ordinal(); // Assuming DiplomacyStatus has a getValue() method.
        int groupTypeValue1 = DiplomacyDB.getDiplomacyStatus(group1, group2).ordinal();
        int groupTypeValue2 = DiplomacyDB.getDiplomacyStatus(group2, group1).ordinal();

        try {
            String sql = "UPDATE shinobi_groups SET group_diplomacy = ? WHERE group_name = ? AND group_type = ?";
            try (PreparedStatement pstmt = ShinobiWay.databaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setInt(1, statusValue);
                pstmt.setString(2, groupName1);
                pstmt.setInt(3, groupTypeValue1);
                int rowsUpdated1 = pstmt.executeUpdate();
                pstmt.setString(2, groupName2);
                pstmt.setInt(3, groupTypeValue2);
                int rowsUpdated2 = pstmt.executeUpdate();

                if (rowsUpdated1 > 0 && rowsUpdated2 > 0) {
                    // Both rows were updated successfully
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Operation failed
    }
    public static boolean tryRivalry(Player player, ShinobiGroup inviterV, String[] args) {
        if (args[1].length() == 0) {
            Utils.error_handler(player, ErrorCode.INVALID_TARGET.ordinal());
        }
        String inviterName = inviterV.getName();
        String invitedName = args[1];
        DiplomacyStatus status = getDiplomacyStatus(inviterV, ShinobiGroupDB.getGroup(args[1]));
        if (status == DiplomacyStatus.ENEMIES) {
            Utils.error_handler(player, ErrorCode.ALREADY_ENEMY.ordinal());
        }
        else {
            setDiplomacy(inviterV,ShinobiGroupDB.getGroup(args[1]), DiplomacyStatus.ENEMIES);
            return true;
        }
        return false;
    }

    public static boolean tryNeutral(Player player, ShinobiGroup group1, String[] args) {
        ShinobiGroup group2 = ShinobiGroupDB.getGroup(args[1]);
        DiplomacyStatus status = getDiplomacyStatus(group1, group2);
        if (status == DiplomacyStatus.ALLIED) {
            setDiplomacy(group1, group2, DiplomacyStatus.NEUTRAL);
        }
        else if (status == DiplomacyStatus.NEUTRAL) {
            Utils.error_handler(player, ErrorCode.ALREADY_NEUTRAL.ordinal());
            return false;
        }
        else {

        }
        return false;
    }

    public static boolean tryAlliance(Player player, ShinobiGroup group1, String[] args) {
        ShinobiGroup group2 = ShinobiGroupDB.getGroup(args[1]);
        DiplomacyStatus status = getDiplomacyStatus(group1, group2);
        if (status == DiplomacyStatus.ENEMIES) {

        }
        else if (status == DiplomacyStatus.ALLIED) {
            Utils.error_handler(player, ErrorCode.ALREADY_ALLY.ordinal());
            return false;
        }
        else {

        }
        return false;
    }
}
