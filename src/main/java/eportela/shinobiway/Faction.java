package eportela.shinobiway;

import java.util.UUID;

public class Faction extends ShinobiGroup{
    public Faction(String groupName, UUID ownerUUID) {
        super(groupName, ownerUUID, GroupType.PARTNERSHIP.ordinal());
    }
}
