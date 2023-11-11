package eportela.shinobiway;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Invitation {
    public static boolean inviteToGroup(Player player, String[] args) {
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
            ShinobiWay.com_handler(player, "You must be the owner of the group to invite players.", 1);
            return false;
        }

        Player target = PlayerUtils.getOnlinePlayer(args[1]);
        if (target == null) {
            ShinobiWay.com_handler(player, "The player you tried to invite either does not exist or is not online!", 1);
            return false;
        }

        UUID playerUUID = target.getUniqueId();
        if (playerUUID.toString().length() == 0) {
            ShinobiWay.com_handler(player, "The player you tried to invite either does not exist or is not online!", 1);
            return false;
        }
        ShinobiGroup temp = ShinobiDB.getPlayerGroup(playerUUID);
        if ( temp != null && temp.getName().length() != 0) {
            ShinobiWay.com_handler(player, "The player you tried to invite is already in a group!", 1);
            return false;
        }
        if (ShinobiWay.getInvitationCodes().containsPlayer(playerUUID.toString()) == null) {
            ShinobiWay.getInvitationCodes().addPlayer(playerUUID.toString());
        }
        ShinobiWay.getInvitationCodes().addInvite(playerUUID.toString(), group.getName());
        ShinobiWay.com_handler(target, "You have been invited to join the group " + group.getName() + ". Type /shinobiGroup accept " + group.getName() + " to accept.", 0);
        ShinobiWay.com_handler(player, "Invitation sent to " + target.getDisplayName() + ".", 0);
        BukkitRunnable expirationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (ShinobiWay.getInvitationCodes().containsPlayer(target.getUniqueId().toString()) != null) {
                    if (ShinobiWay.getInvitationCodes().inviteExists(ShinobiWay.getInvitationCodes().containsPlayer(target.getUniqueId().toString()), group.getName())) {
                        ShinobiWay.getInvitationCodes().removeInvite(target.getUniqueId().toString(), group.getName());
                        if (ShinobiWay.getInvitationCodes().containsPlayer(target.getUniqueId().toString()).size() != 1) {
                            ShinobiWay.getInvitationCodes().removeInvite(target.getUniqueId().toString(), group.getName());
                            ShinobiWay.com_handler(target, "Your invitation from " + player.getName() + " has expired.", 1);
                        }
                    }
                }
            }
        };
        expirationTask.runTaskLater(ShinobiWay.myManager.getPlugin(), 20 * 30);
        return true;
    }

    public static boolean acceptGroupInvite(Player player, String[] args) {
        if (args == null || args.length < 2 || args[1].length() == 0) {
            ShinobiWay.com_handler(player, "Invalid group name! Correct usage: /shinobiGroup accept <group_name>", 1);
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        if (ShinobiWay.getInvitationCodes().containsPlayer(playerUUID.toString()) == null) {
            ShinobiWay.com_handler(player, "No pending group invitation found.", 1);
            return false;
        }

        String groupToJoin = args[1];
        String storedInvitationCode = ShinobiWay.getInvitationCodes().containsPlayer(playerUUID.toString()).toString();
        System.out.println("Stored invitation code: " + storedInvitationCode);
        if (!ShinobiWay.getInvitationCodes().inviteExists(ShinobiWay.getInvitationCodes().containsPlayer(playerUUID.toString()), groupToJoin)) {
            ShinobiWay.com_handler(player, "There is no invite for you from " + groupToJoin, 1);
            return false;
        }
        if (ShinobiDB.addPlayerToGroup(player, groupToJoin)) {
            ShinobiWay.getInvitationCodes().removePlayer(playerUUID.toString());
            ShinobiWay.com_handler(player, "You have successfully joined the group.", 0);
            return true;
        }
        return false;
    }
}