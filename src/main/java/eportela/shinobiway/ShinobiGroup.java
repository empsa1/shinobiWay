package eportela.shinobiway;

import java.util.UUID;

class ShinobiGroup {
    String groupName = null;
    UUID ownerUUID = null;
    public ShinobiGroup(String groupName, UUID ownerUUID, int ordinal) {
        this.ownerUUID = ownerUUID;
        this.groupName = groupName;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getName() {
        return groupName;
    }
}
