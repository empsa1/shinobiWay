package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Invitation {
    private final UUID senderUUID;
    private final UUID receiverUUID;
    private final String villageName;
    private final long creationTime;

    public Invitation(UUID senderUUID, UUID receiverUUID, String villageName) {
        this.senderUUID = senderUUID;
        this.receiverUUID = receiverUUID;
        this.villageName = villageName;
        this.creationTime = System.currentTimeMillis();
    }

    public static void acceptInvite() {
    }

    public static boolean sendInvite(String[] args, Player player, ShinobiGroup group) {
        return true;
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public UUID getReceiverUUID() {
        return receiverUUID;
    }

    public String getVillageName() {
        return villageName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isExpired() {
        // Customize the expiration time as needed (e.g., 30 seconds)
        long currentTime = System.currentTimeMillis();
        long expirationTime = getCreationTime() + 30 * 1000; // 30 seconds
        return currentTime >= expirationTime;
    }

    public void sendInvitation(Player receiver) {
        // Customize the message as you like
        receiver.sendMessage("You've been invited to join the village " + getVillageName());
        receiver.sendMessage("To accept, type /village accept " + getVillageName());
    }
}