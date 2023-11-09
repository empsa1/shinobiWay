package eportela.shinobiway;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InviteList { //0 element of each array is the player-uuid the followings will be the groups inviting him
    private static ArrayList<ArrayList<String>> invitationCodes;
    public InviteList(ArrayList<ArrayList<String>> invitationCodes) {
        this.invitationCodes = invitationCodes;
    }

    public static ArrayList<ArrayList<String>> getInvitationCodes() {
        return invitationCodes;
    }
    public ArrayList<String> containsPlayer(String playerUUID) {
        for (ArrayList<String> strArr : invitationCodes) {
            if (strArr.get(0).equals(playerUUID)) {
                return strArr;
            }
        }
        return null;
    }
    public boolean inviteExists(ArrayList<String> strArr, String groupName) {
        int i = 1;
        while (i < strArr.size()) {
            if (strArr.get(i).equals(groupName)) {
                return true;
            }
        }
        return false;
    }
    public void addPlayer(String playerUUID) {
        if (containsPlayer(playerUUID) != null) {
            return ;
        }
        ArrayList<String> temp = new ArrayList<String>(1);
        temp.add(playerUUID);
        invitationCodes.add(temp);
    }

    public void addInvite(String playerUUID, String groupName) {
        for (ArrayList strArr : invitationCodes) {
            if (strArr.get(0).equals(playerUUID)) {
                strArr.add(groupName);
            }
        }
    }

    public void removeInvite(String playerUUID, String groupName) {
        if (!inviteExists(containsPlayer(playerUUID), groupName)) {
            return ;
        }
        for (ArrayList strArr : invitationCodes) {
            if (strArr.get(0).equals(playerUUID)) {
                strArr.remove(groupName);
            }
        }
    }

    public void removePlayer(String playerUUID) {
        if (containsPlayer(playerUUID) != null) {
            return ;
        }
        for (ArrayList strArr : invitationCodes) {
            if (strArr.get(0).equals(playerUUID)) {
                invitationCodes.remove(strArr);
                return ;
            }
        }
    }
}
