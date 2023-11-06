package eportela.shinobiway;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Village {
    private String name; //Name of the village
    private UUID kageUUID;
    private List<VillageMember> members;

    private double bankBalance;

    public double getBankBalance() {
        return bankBalance;
    }

    public Village(String name) {
        this.name = name;
    }

    public UUID getKageUUID() {
        return kageUUID;
    }

    public void setKageUUID(UUID kageUUID) {
        this.kageUUID = kageUUID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
