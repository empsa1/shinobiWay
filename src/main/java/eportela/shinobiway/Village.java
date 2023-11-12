package eportela.shinobiway;

import java.util.UUID;

public class Village extends ShinobiGroup {
    public Village(String groupName, UUID ownerUUID) {
        super(groupName, ownerUUID, GroupType.PARTNERSHIP.ordinal());
    }
}
