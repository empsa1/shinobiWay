package eportela.shinobiway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ShinobiMember {
    UUID playerUUID;
    ShinobiRank rank;

    public ShinobiMember(UUID playerUUID, ShinobiRank rank) {
        this.playerUUID = playerUUID;
        this.rank = rank;
    }
    public static ShinobiRank getShinobiRank(UUID playerUUID) {
        try {
            String selectRankSql = "SELECT shinobi_rank FROM shinobi_members WHERE player_uuid = ?";
            try (PreparedStatement selectRankStmt = DatabaseManager.getConnection().prepareStatement(selectRankSql)) {
                selectRankStmt.setString(1, playerUUID.toString());
                ResultSet resultSet = selectRankStmt.executeQuery();

                if (resultSet.next()) {
                    int rankOrdinal = resultSet.getInt("shinobi_rank");
                    return ShinobiRank.values()[rankOrdinal];
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ShinobiRank getNextRank(ShinobiRank currentRank) {
        ShinobiRank[] ranks = ShinobiRank.values();
        for (int i = 0; i < ranks.length - 1; i++) {
            if (ranks[i] == currentRank) {
                return ranks[i + 1];
            }
        }

        return null;
    }
    public static ShinobiRank getPreviousRank(ShinobiRank currentRank) {
        ShinobiRank[] ranks = ShinobiRank.values();
        for (int i = 0; i < ranks.length - 1; i++) {
            if (ranks[i] == currentRank) {
                return ranks[i - 1];
            }
        }
        return null;
    }
}
