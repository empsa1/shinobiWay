package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankDB {
    public static int getBank(Player player, ShinobiGroup group) {
        String groupName = group.getName();
        try {
            String sql = "SELECT group_balance FROM shinobi_groups WHERE group_name = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, groupName);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    return (resultSet.getInt("group_balance"));
                }
            }
        } catch (SQLException e) {
            ShinobiWay.com_handler(null, "There was a problem retrieving the bank balance of: " + groupName, 1);
            e.printStackTrace();
        }
        ShinobiWay.com_handler(player, "Something went wrong!", 1);
        return -1;
    }
    public static boolean displayBank(Player player, ShinobiGroup group) {
        String groupName = group.getName();
        try {
            String sql = "SELECT group_balance FROM shinobi_groups WHERE group_name = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, groupName);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    int balance = resultSet.getInt("group_balance");
                    ShinobiWay.com_handler(player, groupName + " balance: " + balance + " Ryo", 0);
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
    public static boolean setBank(Player player, int delta) {
        ShinobiGroup group = ShinobiDB.getPlayerGroup(player.getUniqueId());
        if (ShinobiDB.getPlayerGroup(player.getUniqueId()).getOwnerUUID().toString().equals(player.getUniqueId().toString())) {
            String sql = "UPDATE shinobi_groups SET group_balance = group_balance + ? WHERE group_name = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setInt(1, delta);
                pstmt.setString(2, group.getName());
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    ShinobiWay.com_handler(player, "You have successfully updated your group balance to: " + getBank(player, group), 0);
                    return true;
                } else {
                    ShinobiWay.com_handler(player , "The funds were not updated due to some error!", 1);
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ShinobiWay.com_handler(player, "Something went wrong!", 1);
        return false;
    }
}