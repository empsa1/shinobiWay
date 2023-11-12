package eportela.shinobiway;

public class Town { //Claims the square that belongs to the diagonal set by Coords class
    private static String name = null;
    private static String owner_group;
    private static Coords a = null;
    private static Coords b = null;
    private static TownLevel level;
    public Town(String name, Coords a, Coords b) {
        this.name = name;
        this.a = a;
        this.b = b;
        owner_group = "BANDITS";
        level = TownLevel.RUINS;
        tryClaim(name, a, b);
    }
    public static boolean tryClaim(String town_name, Coords a, Coords b) {
        return false;
    }
    public static boolean tryEvolve(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        if (!TownDB.townExists(name)) {
            return false;
        }
        if (TownDB.getTownLevel(name) < TownLevel.KINGDOM.ordinal()) {
            TownDB.setTownLevel(name, (TownDB.getTownLevel(name)));
            return true;
        }
        return false;
    }
    public static boolean tryUnclaim(String name) {
        if (TownDB.townExists(name)) {
            TownDB.removeTown(name);
            return true;
        }
        return false;
    }
    public static boolean tryNewTownOwner(String name, String group_name) {
        if (TownDB.townExists(name)) {
            TownDB.setNewOwner(name, group_name);
            return true;
        }
        return false;
    }
}