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
        if (group.getName().length() == 0) {
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

        String invitationCode = group.getName();

        // Add or create an invitation code list for the target player
        if (!ShinobiWay.getInvitationCodes().containsKey(playerUUID)) {
            ShinobiWay.getInvitationCodes().put(playerUUID, new ArrayList<>());
        }
        ShinobiWay.getInvitationCodes().get(playerUUID).add(invitationCode);

        ShinobiWay.com_handler(target, "You have been invited to join the group " + group.getName() + ". Type /shinobiGroup accept " + group.getName() + " to accept.", 0);
        ShinobiWay.com_handler(player, "Invitation sent to " + target.getDisplayName() + ".", 0);

        BukkitRunnable expirationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (ShinobiWay.getInvitationCodes().containsKey(target.getUniqueId())) {
                    List<String> invites = ShinobiWay.getInvitationCodes().get(target.getUniqueId());
                    if (invites.contains(invitationCode)) {
                        invites.remove(invitationCode);
                        if (invites.isEmpty()) {
                            ShinobiWay.getInvitationCodes().remove(target.getUniqueId());
                            ShinobiWay.com_handler(target, "Your invitation from " + player.getName() + " has expired.", 1);
                        }
                    }
                }
            }
        };
        expirationTask.runTaskLater(ShinobiWay.myManager.getPlugin(), 20 * 30);

        return true;
    }

    public static boolean acceptGroupInvite(Player target, String[] args) {
        if (args == null || args.length < 2 || args[1].isEmpty()) {
            ShinobiWay.com_handler(target, "Invalid group name! Correct usage: /shinobiGroup accept <group_name>", 1);
            return false;
        }

        UUID playerUUID = target.getUniqueId();
        if (!ShinobiWay.getInvitationCodes().containsKey(playerUUID)) {
            ShinobiWay.com_handler(target, "No pending group invitation found.", 1);
            return false;
        }

        String groupToJoin = args[1];
        String storedInvitationCode = ShinobiWay.getInvitationCodes().get(playerUUID).toString();
        if (!storedInvitationCode.equals(groupToJoin)) {
            ShinobiWay.com_handler(target, "Invalid invitation code or group name.", 1);
            return false;
        }

        ShinobiWay.getInvitationCodes().remove(playerUUID);
        if (ShinobiDB.addPlayerToGroup(target, groupToJoin)) {
            ShinobiWay.com_handler(target, "You have successfully joined the group.", 0);
            return true;
        }
        return false;
    }
}