package eportela.shinobiway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShinobiGroupCommand implements CommandExecutor {
    public static boolean CommandHandler(Player player, ShinobiGroup group, String[] args) {
         if (args[0].equalsIgnoreCase("invite")) {
            return (Invitation.sendInvite(args, player, group));
        } else if (args[0].equalsIgnoreCase("leave")) {
            return (ShinobiDB.removePlayerFromGroup(player, group));
        } else if (args[0].equalsIgnoreCase("bank")) {
            return (ShinobiGroupDB.displayBank(player, group));
        } else if (args[0].equalsIgnoreCase("owner")) {
            return (ShinobiGroupDB.setGroupOwner(player, group, args));
        } else if (args[0].equalsIgnoreCase("ally")) {
            return (DiplomacyDB.tryAlliance(player, group, args));
        } else if (args[0].equalsIgnoreCase("enemy")) {
            return (DiplomacyDB.tryRivalry(player, group, args));
        } else if (args[0].equalsIgnoreCase("neutral")) {
            return (DiplomacyDB.tryNeutral(player, group, args));
        } else if (args[0].equalsIgnoreCase("disband")) {
            return (ShinobiGroupDB.tryDisband(player, group));
        } else if (args[0].equalsIgnoreCase("kick")) {
            return (HierarchyDB.tryKick(player, group, args));
        } else if (args[0].equalsIgnoreCase("demote")) {
            return (HierarchyDB.tryDemote(player, group, args));
        } else if (args[0].equalsIgnoreCase("promote")) {
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
            ShinobiGroup playerGroup = ShinobiDB.getPlayerGroup(player);
            if (playerGroup != null && playerGroup.getName().length() != 0) {
                System.out.println("Going inside CommandHandler");
                return (CommandHandler(player, playerGroup, args));
            } else if (args[0].equalsIgnoreCase("create")) {
                return (ShinobiGroupDB.createGroup(player, args));
            } else if (args[0].equalsIgnoreCase("accept") && args[1].length() != 0) {
                Invitation.acceptInvite();
            } else {
                ShinobiWay.com_handler(player, "You are not able to request that!", 1);
                return false;
            }
            return false;
        }
        ShinobiWay.com_handler(null, "Something went wrong", 1);
        return false;
    }
}