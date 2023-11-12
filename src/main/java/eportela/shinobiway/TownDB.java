package eportela.shinobiway;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TownDB {

    public static Coords[] getTownCoordinates(String townName) {
        Coords townCoords[] = null;
        String sql = "SELECT town_coordsX1, town_coordsY1, town_coordsX2, town_coordsY2 FROM shinobi_towns WHERE town_name = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, townName);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                int x1 = resultSet.getInt("town_coordsX1");
                int y1 = resultSet.getInt("town_coordsY1");
                int x2 = resultSet.getInt("town_coordsX2");
                int y2 = resultSet.getInt("town_coordsY2");
                townCoords = new Coords[]{new Coords(x1, y1), new Coords(x2, y2)};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return townCoords;
    }
    public static boolean townExists(String townName) {
            String sql = "SELECT COUNT(*) FROM shinobi_towns WHERE town_name = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, townName);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return false;
    }
    public static int getTownLevel(String townName) {
        String sql = "SELECT town_level FROM shinobi_towns WHERE town_name = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, townName);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("town_level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static boolean setTownLevel(String townName, int newLevel) {
        String sql = "UPDATE shinobi_towns SET town_level = ? WHERE town_name = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, newLevel);
            pstmt.setString(2, townName);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeTown(String townName) {
        String sql = "DELETE FROM shinobi_towns WHERE town_name = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, townName);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setNewOwner(String townName, String groupName) {
        String sql = "UPDATE shinobi_towns SET town_owner = ? WHERE town_name = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            pstmt.setString(2, townName);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
}
