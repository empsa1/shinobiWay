package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VillageMember {
    UUID playerUUID;
    VillageRank rank;

    public VillageMember(UUID playerUUID, VillageRank rank) {
        this.playerUUID = playerUUID;
        this.rank = rank;
    }

}
