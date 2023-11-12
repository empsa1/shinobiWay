package eportela.shinobiway;

import java.util.ArrayList;

public class Diplomacy { //0 element of each array is the player-uuid the followings will be the groups inviting him
    private static ArrayList<ArrayList<String>> diplomacyCodes;
    public Diplomacy(ArrayList<ArrayList<String>> diplomacyCodes) {
        this.diplomacyCodes = diplomacyCodes;
    }

    public static ArrayList<ArrayList<String>> getDiplomacyCodes() {
        return diplomacyCodes;
    }
    public ArrayList<String> containsGroup(String groupName) {
        for (ArrayList<String> strArr : diplomacyCodes) {
            if (strArr.get(0).equals(groupName)) {
                return strArr;
            }
        }
        return null;
    }
    public boolean diplomacyExists(ArrayList<String> strArr, String groupName) {
        int i = 1;
        while (i < strArr.size()) {
            if (strArr.get(i).equals(groupName)) {
                return true;
            }
        }
        return false;
    }
    public void addGroup(String groupName) {
        if (containsGroup(groupName) != null) {
            return ;
        }
        ArrayList<String> temp = new ArrayList<String>(1);
        temp.add(groupName);
        diplomacyCodes.add(temp);
    }
    public void addInvite(String groupName1, String groupName2) {
        for (ArrayList strArr : diplomacyCodes) {
            if (strArr.get(0).equals(groupName1)) {
                strArr.add(groupName2);
            }
        }
    }
    public void removeInvite(String groupName1, String groupName2) {
        if (! diplomacyExists(containsGroup(groupName1), groupName2)) {
            return ;
        }
        for (ArrayList strArr : diplomacyCodes) {
            if (strArr.get(0).equals(groupName1)) {
                strArr.remove(groupName2);
            }
        }
    }
    public void removeGroup(String groupName) {
        if (containsGroup(groupName) != null) {
            return ;
        }
        for (ArrayList strArr : diplomacyCodes) {
            if (strArr.get(0).equals(groupName)) {
                diplomacyCodes.remove(strArr);
                return ;
            }
        }
    }
}