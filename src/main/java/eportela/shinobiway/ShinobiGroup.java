package eportela.shinobiway;

import java.util.UUID;

class ShinobiGroup {
    String groupName = null; //Name of the village
    UUID ownerUUID = null;

    public ShinobiGroup(String groupName, UUID ownerUUID, int ordinal) {
        this.ownerUUID = ownerUUID;
        this.groupName = groupName;
    }

    public String getName() {
        return groupName;
    }
}
