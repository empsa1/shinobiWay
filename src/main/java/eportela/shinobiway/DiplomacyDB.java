package eportela.shinobiway;

import com.sun.corba.se.impl.transport.SharedCDRContactInfoImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import org.bukkit.plugin.java.JavaPlugin;
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
        return status; // Default to NEUTRAL if status is not found or an error occurs
    }

    public static boolean setDiplomacy(ShinobiGroup group1, ShinobiGroup group2, DiplomacyStatus status) {
        System.out.println("Entered setDiplomacy() with: " + group1.getName() + " " + group2.getName() + " " + status);
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
            ShinobiWay.com_handler(player, "Invalid player", 1);
            return false;
        }
        if (args[1].equals(inviterV.getName())) {
            ShinobiWay.com_handler(player, "You can´t declare war on yourself smart pants!", 1);
            return false;
        }
        DiplomacyStatus status = getDiplomacyStatus(inviterV, ShinobiGroupDB.getGroup(args[1]));
        System.out.println("Current status: " + status);
        if (status == DiplomacyStatus.ENEMIES) {
            ShinobiWay.com_handler(player, "You are already enemy with " + args[1], 1);
            return false;
        } else {
            System.out.println("Entering setDiplomacy()");
            ShinobiWay.com_handler(player, "You have successfully declared " + args[1] + " as your enemy!", 0);
            return (setDiplomacy(inviterV, ShinobiGroupDB.getGroup(args[1]), DiplomacyStatus.ENEMIES));
        }
    }
    public static boolean tryNeutral(Player player, ShinobiGroup group1, String[] args) {
        ShinobiGroup group = ShinobiDB.getPlayerGroup(player.getUniqueId());
        if (group == null || group.getName().length() == 0) {
            ShinobiWay.com_handler(player, "You are not part of a group!", 1);
            return false;
        }
        if (args == null) {
            return false;
        }
        if (args.length < 2 || args[1].isEmpty()) {
            ShinobiWay.com_handler(player, "Invalid player!", 1);
            return false;
        }
        if (!player.getUniqueId().equals(group.getOwnerUUID())) {
            ShinobiWay.com_handler(player, "You must be the owner of the group to neutral with other groups", 1);
            return false;
        }
        ShinobiGroup group2 = ShinobiGroupDB.getGroup(args[1]);
        DiplomacyStatus status = getDiplomacyStatus(group1, group2);
        Player target = Bukkit.getPlayer(group2.getOwnerUUID());
        if (status == DiplomacyStatus.ALLIED) {
            setDiplomacy(group1, group2, DiplomacyStatus.NEUTRAL);
            ShinobiWay.com_handler(player, "You have set your diplomacy with " + group2.getName() + " as neutral!", 0);
            ShinobiWay.com_handler(target, "Your alliance with " + group1.getName() + " is now neutral", 0);
            return true;
        } else if (status == DiplomacyStatus.NEUTRAL) {
            ShinobiWay.com_handler(player, "You are already neutral with: " + group2.getName(), 1);
            return false;
        } else if (target == null) {
            ShinobiWay.com_handler(player, "That group does not exist or it does not have a kage! If you think this is not an error contact devs!", 1);
            return false;
        } else if (RequestDB.alreadyRequest(group, group2)) {
            setDiplomacy(group1, group2, DiplomacyStatus.NEUTRAL);
            ShinobiWay.com_handler(player, "You have set your diplomacy with " + group2.getName() + " as neutral!", 0);
            ShinobiWay.com_handler(target, "Your alliance with " + group1.getName() + " is now neutral", 0);
        }
            RequestDB.createRequest(group, group2, DiplomacyStatus.NEUTRAL); //Request Table : group1_name group2_name request_Id Neutral --> delete when done
            ShinobiWay.com_handler(target, group.getName() +
                    " has proposed an neutral condition to: " + group2.getName() + ".\nTo accept do /shinobigroup neutral <group name>", 0);
            return true;
    }
    public static boolean tryAlliance(Player player, ShinobiGroup group1, String[] args) {
        ShinobiGroup group = ShinobiDB.getPlayerGroup(player.getUniqueId());
        if (group == null || group.getName().length() == 0) {
            ShinobiWay.com_handler(player, "You are not part of a group!", 1);
            return false;
        }
        if (args == null) {
            return false;
        }
        if (args.length < 2 || args[1].isEmpty()) {
            ShinobiWay.com_handler(player, "Invalid player!", 1);
            return false;
        }
        if (!player.getUniqueId().equals(group.getOwnerUUID())) {
            ShinobiWay.com_handler(player, "You must be the owner of the group to ally with other groups", 1);
            return false;
        }
        ShinobiGroup group2 = ShinobiGroupDB.getGroup(args[1]);
        DiplomacyStatus status = getDiplomacyStatus(group1, group2);
        Player target = Bukkit.getPlayer(group2.getOwnerUUID());
        if (status == DiplomacyStatus.ALLIED) {
            ShinobiWay.com_handler(player, "You are already allied with " + group2.getName(), 1);
            return false;
        } else if (target == null) {
            ShinobiWay.com_handler(player, "That group does not exist or it does not have a kage! If you think this is not an error contact devs!", 1);
            return false;
        } else if (status == DiplomacyStatus.NEUTRAL || status == DiplomacyStatus.ENEMIES) {
            if (RequestDB.alreadyRequest(group2, group)) {
                DiplomacyDB.setDiplomacy(group, group2, DiplomacyStatus.ALLIED);
                ShinobiWay.com_handler(player, "You are now allied with " + group2.getName(), 0);
                ShinobiWay.com_handler(target, "You are now allied with " + group.getName(), 0);
                return true;
            }
            RequestDB.createRequest(group, group2, DiplomacyStatus.ALLIED);
            ShinobiWay.com_handler(target, group.getName() +
                    " has proposed an alliance condition to: " + group2.getName() + ".\nTo accept do /shinobigroup ally <group name>", 0);
            return true;
        }
        ShinobiWay.com_handler(player, "Something went wrong!", 1);
        return false;
    }
}
