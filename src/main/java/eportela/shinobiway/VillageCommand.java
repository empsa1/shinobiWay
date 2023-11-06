package eportela.shinobiway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;
import java.util.Objects;
import java.util.UUID;

public class VillageCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Village playerVillage = PlayerDB.getPlayerVillage(player.getUniqueId().toString());
            UUID playerUUID = player.getUniqueId();
            boolean isKage = false;
            if (playerVillage != null && (playerUUID == Objects.requireNonNull(playerVillage).getKageUUID())) {
                isKage = true;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                Invitation.acceptInvite();
            } else if (args[0].equalsIgnoreCase("create")) {
                if (args[1].length() != 0) {
                    if (playerVillage != null) {
                        ShinobiWay.communication_handler(player, "You are already in a village!", 1);
                    } else if (VillageDB.villageAlreadyExists(args[1])) {
                        ShinobiWay.communication_handler(player, "The name you are using to create your village already is in use", 1);
                    } else {
                        VillageUtils.createVillage(player, args[1]);
                    }
                }
            } else if (playerVillage == null) {
                ShinobiWay.communication_handler(player, "You are not apart of a village!", 1);
            } else if (args[0].equalsIgnoreCase("invite") && args[1].length() != 0 && isKage) {
                Player invited = PlayerUtils.getOnlinePlayer(args[1]);
                if (invited == null) {
                    ShinobiWay.communication_handler(player, "The player you tried to invited does not exist!", 1);
                    return false;
                }
                if (PlayerDB.getPlayerVillage(invited.getUniqueId().toString()) == null) {
                    VillageUtils.sendInvite(player, invited, playerVillage);
                } else {
                    ShinobiWay.communication_handler(player, invited.getDisplayName() + " is already in a village", 1);
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                PlayerDB.removePlayerFromVillage(player);
            } else if (args[0].equalsIgnoreCase("bank") && isKage) {
                VillageDB.displayBank(playerVillage);
            } else if (args[0].equalsIgnoreCase("kage") && args[1].length() != 0) {
                if (!isKage) {
                    ShinobiWay.communication_handler(player, "You don´t have permission to choose a new Kage for your village!", 1);
                    return false;
                }
                Player chosen = PlayerUtils.getOnlinePlayer(args[1]);
                if (chosen == null) {
                    ShinobiWay.communication_handler(player, chosen.getDisplayName() + " does not exist!", 1);
                    return false;
                }
                if (!PlayerDB.getPlayerVillage(chosen.getUniqueId().toString()).equals(playerVillage.getName())) {
                    ShinobiWay.communication_handler(player, chosen.getDisplayName() + " is not apart of your village!", 1);
                    return false;
                }
                VillageDB.setKage(chosen, playerVillage.getName());
            } else if (args[0].equalsIgnoreCase("war") && isKage) {
                if (args.length == 1) {
                    openWarGUI(player);
                } else {
                    player.sendMessage("Usage: /village war");
                }
            } else {
                player.sendMessage("USAGE");
            }
        }
        return true;
    }

    private void openWarGUI(Player player) {

    }

    private boolean isKageOrAdvisor(Player player) {

        return false;
    }

    private boolean isKage(Player player) {

        return false;
    }

    private void createVillage(Player player, String villageName) {

    }

    private void invitePlayerToVillage(Player sender, String playername) {

    }

    private void leaveVillage(Player player) {

    }

    private void displayBankBalance(Player player) {

    }

    private void openUpgradeGUI(Player player) {

    }

    private void nominateNewKage(Player kage) {

    }

    private double getVillageBalance(Village village) {

        return 0;
    }
}