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
        DiplomacyStatus status = DiplomacyStatus.NEUTRAL;
        try {
            String sql = "SELECT diplomacy_type FROM diplomacy WHERE (group_name1 = ? AND group_name2 = ?) OR (group_name2 = ? AND group_name1 = ?)";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, groupName1);
                pstmt.setString(2, groupName2);
                pstmt.setString(3, groupName2);
                pstmt.setString(4, groupName1);

                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    status = DiplomacyStatus.values()[resultSet.getInt("diplomacy_type")];
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  status; // Default to NEUTRAL if status is not found or an error occurs
    }

    public static boolean setDiplomacy(ShinobiGroup group1, ShinobiGroup group2, DiplomacyStatus status) {
        System.out.println("Entered setDiplomacy() with: " + group1.getName() +  " " + group2.getName() + " " + status);
        String groupName1 = group1.getName();
        String groupName2 = group2.getName();
        int statusValue = status.ordinal(); // Assuming DiplomacyStatus has a getValue() method.
        String sql = "INSERT INTO diplomacy (group_name1, group_name2, diplomacy_type) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE diplomacy_type = VALUES(diplomacy_type)";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, groupName1);
            pstmt.setString(2, groupName2);
            pstmt.setInt(3, statusValue);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static boolean tryRivalry(Player player, ShinobiGroup inviterV, String[] args) {
        System.out.println("Inside tryRivalry info: " + player.getDisplayName() + inviterV.getName() + args[1]);
        if (args[1].length() == 0) {
            Utils.error_handler(player, ErrorCode.INVALID_TARGET.ordinal());
            return false;
        }
        if (args[1].equals(inviterV.getName())) {
            ShinobiWay.com_handler(player, "You can´t declare war on yourself smart pants!", 1);
            return false;
        }
        DiplomacyStatus status = getDiplomacyStatus(inviterV, ShinobiGroupDB.getGroup(args[1]));
        System.out.println("Current status: " + status);
        if (status == DiplomacyStatus.ENEMIES) {
            Utils.error_handler(player, ErrorCode.ALREADY_ENEMY.ordinal());
        }
        else {
            System.out.println("Entering setDiplomacy()");
            ShinobiWay.com_handler(player, "You have successfully declared " + args[1] + " as your enemy!", 0);
            return (setDiplomacy(inviterV,ShinobiGroupDB.getGroup(args[1]), DiplomacyStatus.ENEMIES));
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
            return true;
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
