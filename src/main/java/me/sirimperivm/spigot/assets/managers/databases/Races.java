package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;

import java.sql.*;
import java.util.HashMap;

@SuppressWarnings("all")
public class Races {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = data.getMods();

    static Connection conn = data.conn;
    String dbName = data.dbName;
    String tablePrefix = data.tablePrefix;
    String tableName = "races";
    String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[]{"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile capire se la tabella delle razze è presente nel database!"));
            e.printStackTrace();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `raceId` TEXT NOT NULL, `raceName` TEXT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log(Main.getSuccessPrefix() + Colors.text("Database delle razze creato con successo!"));
            } catch (SQLException e) {
                log(Main.getFailPrefix() + Colors.text("Impossibile creare il database delle razze!"));
                e.printStackTrace();
                plugin.setCanConnect(false);
                plugin.disablePlugin();
            }
        }
    }

    public void insertRaceData(String raceId, String raceName) {
        String query = "INSERT INTO " + database + "(raceId, raceName) VALUES (?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, raceId);
            state.setString(2, raceName);
            state.executeUpdate();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile inserire un dato nella tabella delle razze: " +
                    "\n RaceId: " + raceId +
                    "\n RaceName: " + raceName +
                    "\n...!"));
            e.printStackTrace();
        }
    }

    public void deleteRace(String raceName) {
        String query = "DELETE FROM " + database + " WHERE raceName='" + raceName + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("E' stato impossibile eliminare la razza: " + raceName + "!"));
            e.printStackTrace();
        }
    }

    public String getRaceName(String raceId) {
        String raceName = "N/A";
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("raceId").equalsIgnoreCase(raceId)) {
                    raceName = rs.getString("raceName");
                    break;
                }
            }
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("E' stato impossibile ottenere il nome di una razza dall'id: " + raceId + "!"));
            e.printStackTrace();
        }
        return raceName;
    }

    public String getRaceId(String raceName) {
        String raceId = "N/A";
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("raceName").equalsIgnoreCase(raceName)) {
                    raceId = rs.getString("raceId");
                    break;
                }
            }
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("E' stato impossibile ottenere l'id di una razza dal nome: " + raceName + "!"));
            e.printStackTrace();
        }
        return raceId;
    }

    public HashMap<String, String> getGeneratedId() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                hashMap.put(rs.getString("raceId"), rs.getString("raceName"));
            }
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile ottenere la lista degli id delle razze create."));
            e.printStackTrace();
        }
        return hashMap;
    }
}
