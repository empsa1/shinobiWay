package eportela.shinobiway;

import org.bukkit.entity.Player;

public class VillageUtils {

    public static void createVillage(Player player, String villageName) {
        Village village = new Village(villageName);
        VillageDB.addVillage(village);
        PlayerDB.addPlayerToVillageDB(player, village);
        VillageDB.setKage(player, village.getName());
        village.setKageUUID(player.getUniqueId());
        ShinobiWay.communication_handler(player, "You have successfully created a new village named: " + villageName, 0);
        ShinobiWay.communication_handler(null, player.getDisplayName() + " has created a new village named: " + villageName, 0);
    }

    public static void sendInvite(Player inviter, Player invited, Village village) {
        Invitation invitation = new Invitation(invited.getUniqueId(), invited.getUniqueId(), village.getName());
        invitation.sendInvitation(invited);
        ShinobiWay.communication_handler(inviter, "Invitation sent to: " + invited.getDisplayName() + ". They have 30 seconds to accept", 0);
    }
}
