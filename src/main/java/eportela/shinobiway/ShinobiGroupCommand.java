package eportela.shinobiway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShinobiGroupCommand implements CommandExecutor {
    public static boolean CommandHandler(Player player, ShinobiGroup group, String[] args) {
          if (args[0].equalsIgnoreCase("invite")) { //needs work
            return (Invitation.inviteToGroup(player, args));
        } else if (args[0].equalsIgnoreCase("leave")) { //working
            return (ShinobiDB.removePlayerFromGroup(player, group));
        } else if (args[0].equalsIgnoreCase("bank")) { //working
            return (ShinobiGroupDB.displayBank(player, group));
        } else if (args[0].equalsIgnoreCase("owner")) { //maybe
            return (ShinobiGroupDB.setGroupOwner(player, group, args));
        } else if (args[0].equalsIgnoreCase("ally")) {
            return (DiplomacyDB.tryAlliance(player, group, args));
        } else if (args[0].equalsIgnoreCase("enemy")) { //working
            return (DiplomacyDB.tryRivalry(player, group, args));
        } else if (args[0].equalsIgnoreCase("neutral")) {
            return (DiplomacyDB.tryNeutral(player, group, args));
        } else if (args[0].equalsIgnoreCase("disband")) { //working
            return (ShinobiGroupDB.tryDisband(player, group));
        } else if (args[0].equalsIgnoreCase("kick")) { //working
            return (HierarchyDB.tryKick(player, group, args));
        } else if (args[0].equalsIgnoreCase("demote")) { //maybe
            return (HierarchyDB.tryDemote(player, group, args));
        } else if (args[0].equalsIgnoreCase("promote")) { //maybe
            return (HierarchyDB.tryPromote(player, group, args));
        } else if (args[0].equalsIgnoreCase("war")) {
            return (War.tryOpenWARGUI(player, group, args));
        }
        Utils.error_handler(player, ErrorCode.USAGE.ordinal());
        return false;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ShinobiGroup playerGroup = ShinobiDB.getPlayerGroup(player.getUniqueId());
            if (args == null || args[0].length() == 0) {
                ShinobiWay.com_handler(player, "Invalid input!", 1);
                return false;
            }
            else if (playerGroup != null && playerGroup.getName().length() != 0) {
                System.out.println("Going inside CommandHandler");
                return (CommandHandler(player, playerGroup, args));
            } else if (args[0].equalsIgnoreCase("create")) { //working
                return (ShinobiGroupDB.createGroup(player, args));
            } else if (args[0].equalsIgnoreCase("accept")) { //maybe
                Invitation.acceptGroupInvite(player, args); //working
            } else {
                ShinobiWay.com_handler(player, "You are not apart of a group!", 1);
                return false;
            }
            return false;
        }
        ShinobiWay.com_handler(null, "Something went wrong", 1);
        return false;
    }
}