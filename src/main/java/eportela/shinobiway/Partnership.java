package eportela.shinobiway;

import java.util.UUID;

public class Partnership extends ShinobiGroup {
    public Partnership(String groupName, UUID ownerUUID) {
        super(groupName, ownerUUID, GroupType.PARTNERSHIP.ordinal());
    }
}
